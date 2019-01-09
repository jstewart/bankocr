(ns bankocr.core
  (:require [clojure.spec.alpha :as s]))

(s/def ::cell-row
  (s/coll-of char? :count 3))

(s/def ::cell
  (s/coll-of ::cell-row :count 3))

(defn cell->number
  "reads a number from a 3x3 cell"
  [cell]
  ;; For now we just fail the test
  false)

(s/fdef cell->number
  :args ::cell
  :ret number?
  :fn #(and (>= % 0)
            (<= % 9)))
