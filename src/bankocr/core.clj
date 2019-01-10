(ns bankocr.core
  (:require [clojure.spec.alpha :as s]))

(s/def ::cell-row
  (s/coll-of string? :count 3))

(s/def ::cell
  (s/coll-of ::cell-row :count 3))

(defn cell->number
  "identifies a number from a 3x3 cell of characters"
  [cell]
  (condp = cell
    '((" " "_" " ") ("|" " " "|") ("|" "_" "|")) 0
    '((" " " " " ") (" " " " "|") (" " " " "|")) 1
    '((" " "_" " ") (" " "_" "|") ("|" "_" " ")) 2
    '((" " "_" " ") (" " "_" "|") (" " "_" "|")) 3
    '((" " " " " ") ("|" "_" "|") (" " " " "|")) 4
    '((" " "_" " ") ("|" "_" " ") (" " "_" "|")) 5
    '((" " "_" " ") ("|" "_" " ") ("|" "_" "|")) 6
    '((" " "_" " ") (" " " " "|") (" " " " "|")) 7
    '((" " "_" " ") ("|" "_" "|") ("|" "_" "|")) 8
    '((" " "_" " ") ("|" "_" "|") (" " "_" "|")) 9
    nil))

(s/fdef cell->number
  :args (s/cat :cell ::cell)
  :ret (s/or :nil nil?
             :numeric number?)
  :fn (s/or :nil nil?
            :range (s/and #(>= (:ret %) 5)
                          #(<= (:ret %) 9))))
