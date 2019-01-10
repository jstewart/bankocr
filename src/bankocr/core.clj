(ns bankocr.core
  (:require [clojure.spec.alpha :as s]))

(s/def ::cell-row
  (s/coll-of char? :count 3))

(s/def ::cell
  (s/coll-of ::cell-row :count 3))

(defn cell->number
  "identifies a number from a 3x3 cell of characters"
  [cell]
  (condp = cell
    '((\space \_ \space) (\| \space \|) (\| \_ \|))                 0
    '((\space \space \space) (\space \space \|) (\space \space \|)) 1
    '((\space \_ \space) (\space \_ \|) (\| \_ \space))             2
    '((\space \_ \space) (\space \_ \|) (\space \_ \|))             3
    '((\space \space \space) (\| \_ \|) (\space \space \|))         4
    '((\space \_ \space) (\| \_ \space) (\space \_ \|))             5
    '((\space \_ \space) (\| \_ \space) (\| \_ \|))                 6
    '((\space \_ \space) (\space \space \|) (\space \space \|))     7
    '((\space \_ \space) (\| \_ \|) (\| \_ \|))                     8
    '((\space \_ \space) (\| \_ \|) (\space \_ \|))                 9))

(s/fdef cell->number
  :args ::cell
  :ret number?
  :fn #(and (>= % 0)
            (<= % 9)))
