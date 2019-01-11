(ns bankocr.core-test
  (:require [clojure.spec.test.alpha :as stest]
            [clojure.spec.alpha :as s]
            [clojure.test :refer [deftest testing is are]]
            [bankocr.core :as c]
            [clojure.java.io :as io]))

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


(s/def ::textual-digit string?)

(defn textual-to-cell
  "turns a `textual` representation of a digit into a 3x3 cell"
  [textual]
  (->> textual
       seq
       (map str)
       (partition 3)))

(s/fdef textual-to-cell
  :args (s/cat :textual ::textual-digit)
  :ret ::c/cell)

(stest/instrument [`textual-to-cell
                   `cell->number
                   `partition-file])

;; Tests to ensure that numbers 0-9 are correctly identified
(deftest cell->number_test
  (testing "detecting a zero"
    (is (= 0 (c/cell->number
              (textual-to-cell zero)))))

  (testing "detecting a one"
    (is (= 1 (c/cell->number
              (textual-to-cell one)))))

  (testing "detecting a two"
    (is (= 2 (c/cell->number
              (textual-to-cell two)))))

  (testing "detecting a three"
    (is (= 3 (c/cell->number
              (textual-to-cell three)))))

  (testing "detecting a four"
    (is (= 4 (c/cell->number
              (textual-to-cell four)))))

  (testing "detecting a five"
    (is (= 5 (c/cell->number
              (textual-to-cell five)))))

  (testing "detecting a six"
    (is (= 6 (c/cell->number
              (textual-to-cell six)))))

  (testing "detecting a seven"
    (is (= 7 (c/cell->number
              (textual-to-cell seven)))))

  (testing "detecting a eight"
    (is (= 8 (c/cell->number
              (textual-to-cell eight)))))

  (testing "detecting a nine"
    (is (= 9 (c/cell->number
              (textual-to-cell nine)))))

  (testing "detecting garbage"
    (is (nil? (c/cell->number
               '(("" "" "")
                 ("" "" "")
                 ("" "" "")))))))

(deftest parition-file-test
  (testing "with a valid file"
    (let [partitioned (c/partition-file
                      (io/resource "fixtures/accounts.txt"))]
      (testing "partitions account numbers (27 chars x 3 lines)"
        (is (= 11 (count partitioned)))
        (is (= 3 (count (first partitioned))))
        (is (= 27 (count (ffirst partitioned)))))))
  (testing "with an invalid file"
    (let [partitioned (c/partition-file
                       (io/resource "fixtures/invalid.txt"))]
      (testing "is an empty list"
        (is (empty? partitioned))))))

(deftest line-cells-test
  (testing "with a valid account number line"
    (let [line (-> "fixtures/accounts.txt"
                   io/resource
                   c/partition-file
                   first)]
      (testing "creates 3x3 cells representing digits"
        (s/explain ::c/cell (c/line->cells line))
        (is (s/valid?
             ::c/cell (c/line->cells line)))))))
