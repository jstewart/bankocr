(ns bankocr.core
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]))

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
            :range #(<= 0 (:ret %) 9)))

(s/def ::reader-type
  (fn [path]
    (try
      (io/reader path)
      true
      (catch Exception _ false))))

(s/def ::account-number-line
  (s/coll-of string? :count 3))

(s/def ::account-numbers
  (s/coll-of ::account-number-line))

(s/def ::empty-list
  (s/and seq?
         empty?))

(defn partition-file
  "partitions `file` into textual account
  number representations"
  [file]
  (->> file
      io/reader
      line-seq
      (partition 4)
      (map butlast)))

(s/fdef partition-file
  :args (s/cat :file ::reader-type)
  :ret (s/or
        :account-numbers ::account-numbers
        :invalid ::empty-list))

(defn line->cells
  "creates a 3x3 cell out of a seq (`line`)
  that conforms to the ::account-number-line spec
  "
  [line]
  (->> line
       (map #(partition 3 %))
       (map #(map str %))
       (apply map list)))

(s/fdef line->cells
  :args (s/cat
         :line ::account-number-line)
  :ret (s/or
        :cell ::cell
        :invalid ::empty-list))
