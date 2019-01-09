(ns bankocr.user-story-1-test
  (:require [clojure.test :refer [deftest testing is are]]
            [bankocr.core :refer [cell->number]]))

;; Tests to ensure that numbers 0-9 are correctly identified
(deftest cell->number_test
  (testing "detecting a zero"
    (let [textual-zero (str " _ "
                            "| |"
                            "|_|")
          cell (->> textual-zero
                    seq
                    (partition 3))]
      (is (= 0 (cell->number cell))))))
