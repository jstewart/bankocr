(ns bankocr.core-test
  (:require [clojure.test :refer [deftest testing is are]]
            [bankocr.core :refer [cell->number]]))

(defn textual-to-cell
  "turns a `textual` representation of a digit into a 3x3 cell"
  [textual]
  (->> textual
       seq
       (map str)
       (partition 3)))

;; Textual representations of numbers (OCR cell)
;; Attempts made to line the strings up may have the best results
;; by adjusting to a monospaced font
(def zero  (str " _ "
                "| |"
                "|_|"))

(def one   (str "   "
                "  |"
                "  |"))

(def two   (str " _ "
                " _|"
                "|_ "))

(def three (str " _ "
                " _|"
                " _|"))

(def four  (str "   "
                "|_|"
                "  |"))

(def five  (str " _ "
                "|_ "
                " _|"))

(def six   (str " _ "
                "|_ "
                "|_|"))

(def seven (str " _ "
                "  |"
                "  |"))

(def eight (str " _ "
                "|_|"
                "|_|"))

(def nine  (str " _ "
                "|_|"
                " _|"))

(textual-to-cell zero)


;; Tests to ensure that numbers 0-9 are correctly identified
(deftest cell->number_test
  (testing "detecting a zero"
    (is (= 0 (cell->number
              (textual-to-cell zero)))))

  (testing "detecting a one"
    (is (= 1 (cell->number
              (textual-to-cell one)))))

  (testing "detecting a two"
    (is (= 2 (cell->number
              (textual-to-cell two)))))

  (testing "detecting a three"
    (is (= 3 (cell->number
              (textual-to-cell three)))))

  (testing "detecting a four"
    (is (= 4 (cell->number
              (textual-to-cell four)))))

  (testing "detecting a five"
    (is (= 5 (cell->number
              (textual-to-cell five)))))

  (testing "detecting a six"
    (is (= 6 (cell->number
              (textual-to-cell six)))))

  (testing "detecting a seven"
    (is (= 7 (cell->number
              (textual-to-cell seven)))))

  (testing "detecting a eight"
    (is (= 8 (cell->number
              (textual-to-cell eight)))))

  (testing "detecting a nine"
    (is (= 9 (cell->number
              (textual-to-cell nine))))))
