(ns pom)

;; /*
;;  * Phase of the Moon.  Calculates the current phase of the moon.
;;  * Based on routines from `Practical Astronomy with Your Calculator',
;;  * by Duffett-Smith.  Comments give the section from the book that
;;  * particular piece of code was adapted from.
;;  *
;;  * -- Keith E. Brandt  VIII 1984
;;  *
;;  * Updated to the Third Edition of Duffett-Smith's book, IX 1998
;;  *
;;  */

(def EPOCH 1990)
(def EPSILONg 279.403303) ; solar ecliptic long at EPOCH
(def RHOg 282.768422) ; solar ecliptic long of perigee at EPOCH
(def ECCEN 0.016713) ; solar orbit eccentricity
(def lzero 318.351648) ; lunar mean long at EPOCH
(def Pzero 36.340410) ; lunar mean long of perigee at EPOCH
(def Nzero 318.510107) ; lunar mean long of node at EPOCH

(defn- adj360 [deg]
  (cond (< deg 0) (recur (+ deg 360))
        (> deg 360) (recur (- deg 360))
        true deg))

(defn- sin [value] (Math/sin value))
(defn- cos [value] (Math/cos value))
(defn- dtor [value] (/ (* value Math/PI) 180))

(defn- -days-since-epoch [cal]
  (+ (.get cal java.util.Calendar/DAY_OF_YEAR)
     (/ (- (+ (.get cal java.util.Calendar/HOUR_OF_DAY)
              (/ (.get cal java.util.Calendar/MINUTE) 60)
              (/ (.get cal java.util.Calendar/SECOND) 3600))
           (/ (+ (.get cal java.util.Calendar/ZONE_OFFSET)
                 (.get cal java.util.Calendar/DST_OFFSET))
              3600000))
        24)
     (reduce + (map #(if (. cal isLeapYear %) 366 365)
                    (range EPOCH (.get cal java.util.Calendar/YEAR))))))

(defmulti days-since-epoch class)

(defmethod days-since-epoch java.util.Calendar [cal]
  (-days-since-epoch cal))

(defmethod days-since-epoch java.util.Date [date]
  (-days-since-epoch (doto (java.util.Calendar/getInstance) (.setTime date))))

(defmethod days-since-epoch java.lang.Long [millis]
  (-days-since-epoch (doto (java.util.Calendar/getInstance) (.setTimeInMillis millis))))

(defn- pom [days]
  (let [N (adj360 (/ (* 360 days) 365.242191))
        Msol (adj360 (- (+ N EPSILONg) RHOg))
        Ec (adj360 (* (/ 360 Math/PI) ECCEN (sin (dtor Msol))))
	LambdaSol (adj360 (+ N Ec EPSILONg))
	l (adj360 (+ (* 13.1763966 days) lzero))
        Mm (adj360 (- l (* 0.1114041 days) Pzero))
	Nm (adj360 (- Nzero (* 0.0529539 days)))
        Ev (* 1.2739 (sin (dtor (- (* 2 (- l LambdaSol)) Mm))))
	Ac (* 0.1858 (sin (dtor Msol)))
	A3 (* 0.37 (sin (dtor Msol)))
	Mmprime (- (+ Mm Ev) Ac A3)
	Ecc (* 6.2886 (sin (dtor Mmprime)))
	A4 (* 0.214 (sin (dtor (* 2 Mmprime))))
	lprime (- (+ l Ev Ecc) (+ Ac A4))
	V (* 0.6583 (sin (dtor (* 2 (- lprime LambdaSol)))))
	ldprime (+ lprime V)
	D (- ldprime LambdaSol)]
    (* 50.0 (- 1 (cos (dtor D))))))

(defn phase-of-the-moon
  "Phase of the moon in percentage of full."
  ([] (pom (days-since-epoch (java.util.Calendar/getInstance))))
  ([time] (pom (days-since-epoch time))))


;; Original:
;;
;; /*	$OpenBSD: pom.c,v 1.12 2005/11/05 21:25:00 jmc Exp $	*/
;; /*    $NetBSD: pom.c,v 1.6 1996/02/06 22:47:29 jtc Exp $      */

;; /*
;;  * Copyright (c) 1989, 1993
;;  *	The Regents of the University of California.  All rights reserved.
;;  *
;;  * This code is derived from software posted to USENET.
;;  *
;;  * Redistribution and use in source and binary forms, with or without
;;  * modification, are permitted provided that the following conditions
;;  * are met:
;;  * 1. Redistributions of source code must retain the above copyright
;;  *    notice, this list of conditions and the following disclaimer.
;;  * 2. Redistributions in binary form must reproduce the above copyright
;;  *    notice, this list of conditions and the following disclaimer in the
;;  *    documentation and/or other materials provided with the distribution.
;;  * 3. Neither the name of the University nor the names of its contributors
;;  *    may be used to endorse or promote products derived from this software
;;  *    without specific prior written permission.
;;  *
;;  * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
;;  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
;;  * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
;;  * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
;;  * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
;;  * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
;;  * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
;;  * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
;;  * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
;;  * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
;;  * SUCH DAMAGE.
;;  */