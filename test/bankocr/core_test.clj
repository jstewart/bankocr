(ns bankocr.core-test
  (:require [clojure.spec.test.alpha :as stest]
            [clojure.spec.alpha :as s]
            [clojure.test :refer [deftest testing is are]]
            [bankocr.core :as c]
            [clojure.java.io :as io]))

;; Textual representations of numbers (OCR cell)
;; Attempts made to line the strings up may have the best results
;; by adjusting to a monospaced font

:ret boolean?(def zero  (str " _ "
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

(stest/instrument [`c/textual-to-cell
                   `c/cell->number
                   `c/partition-file
                   `c/file->account-numbers
                   `c/account-number-string
                   `c/valid-account?
                   `c/generate-output-file])

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
    (is (= "?" (c/cell->number
                '(("" "" "")
                  ("" "" "")
                  ("" "" "")))))))

(deftest partition-file-test
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
        (is (s/valid?
             ::c/cell-line (c/line->cells line))))))

  (testing "with an invalid account number line"
    (let [invalid-line [[" " "_" " "] ["" 1]]]
      (testing "is an empty list"
        (is (= '() (c/line->cells invalid-line)))))))

(deftest account-number-string-test
  (testing "with a valid cell group"
    (testing "creates a full account number from a group of cells"
      (is (= "000000000"
             (c/account-number-string
              '(((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))))))))

  (testing "with illegible numbers"
    (testing "it replaces illegible digits with a question mark"
      (is (= "0?0000000"
             (c/account-number-string
              '(((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                 ((" " "_" " ") ("|" " " " ") ("_" "_" "|"))
                 ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                 ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                 ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                 ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                 ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                 ((" " "_" " ") ("|" " " "|") ("|" "_" "|"))
                 ((" " "_" " ") ("|" " " "|") ("|" "_" "|")))))))))

(deftest account-number-status-test
  (testing "with a valid account number"
    (is (= "" (c/account-number-status "457508000"))))
  (testing "with an illegible account number"
    (is (= "ILL" (c/account-number-status "0?0000000"))))
  (testing "with an invalid account number"
    (is (= "ERR" (c/account-number-status "664371495")))))

;; With all of the little details out of the way we can get to
;; fulfilling the first user story.
;;
;; User Story 1
;; Write a program that parses an account number file into actual account numbers.
(deftest user-story-1-test
  (testing "it parses a file into a collection of account numbers"
    (let [expected ["000000000"
                    "111111111"
                    "222222222"
                    "333333333"
                    "444444444"
                    "555555555"
                    "666666666"
                    "777777777"
                    "888888888"
                    "999999999"
                    "123456789"]
          actual  (-> "fixtures/accounts.txt"
                      io/resource
                      c/file->account-numbers)]
      (is (= expected actual)))))

;; User Story 2
;; Checksums to ensure valid account numbers
(deftest user-story-2-test
  (testing "with a valid account number"
    (is (true? (c/valid-account? "457508000"))))

  (testing "with an invalid account number"
    (is (false? (c/valid-account? "664371495")))))

;; User Story 3
;; Write a file with results of the OCR. Illegible digits are replaced with ?.
;; If there's an incorrect checksum or one of the digits is illegible,
;; it's indicated in a second column
(deftest user-story-3-test
  (testing "writes an output file containing the scan results"
    (let [input (io/resource "fixtures/user-story-3.txt")
          output "output.txt"]
      (c/generate-output-file
       input
       (-> output io/file io/writer))
      (is (.exists (io/file output)))))

  (testing "output file has the expected results"
    (let [expected ["000000000  "
                    "111111?11  ILL"
                    "222222222  ERR"
                    "333333333  ERR"
                    "999?99999  ILL"
                    "123456789  "]
          output-file  "output.txt"]
      (is (= expected (->> output-file
                           io/reader
                          line-seq))))))
