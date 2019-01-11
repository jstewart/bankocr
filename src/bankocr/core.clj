(ns bankocr.core
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]))

(s/def ::cell-row
  (s/coll-of string? :count 3))

(s/def ::cell
  (s/coll-of ::cell-row :count 3))

(s/def ::cell-line
  (s/coll-of ::cell :count 9))

(s/def ::reader-type
  (fn [path]
    (try
      (io/reader path)
      true
      (catch Exception _ false))))

(s/def ::account-number
  (s/and string? #(= 9 (count %))))

(s/def ::account-numbers
  (s/coll-of ::account-number))

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

(s/def ::account-number-line
  (s/coll-of string? :count 3))

(s/def ::account-number-lines
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
        :account-number-lines ::account-number-lines
        :invalid ::empty-list))

(defn line->cells
  "creates a 3x3 cell out of a seq (`line`)
  that conforms to the ::account-number-line spec"
  [line]
  (->> line
       (map (fn [cell]
              (->> cell
                   (partition 3)
                   (map #(map str %)))))
       (apply map list)))

(s/fdef line->cells
  :args (s/cat
         :line ::account-number-line)
  :ret (s/or
        :cell ::cell
        :invalid ::empty-list))

(defn account-number-string
  "creates an account number string from a `cell-group`"
  [cell-group]
  (reduce (fn [acct cell]
            (str acct (cell->number cell)))
          ""
          cell-group))

(s/fdef account-number-string
  :args (s/cat :cell-group ::cell-line)
  :ret (s/or :account-number ::account-number
             :invalid nil?))

(defn valid-account?
  "calculates a checksum for `account-number`
  to determine validity"
  [account-number]
  (let [sum (->> account-number
                 (re-seq #"\d")
                 (map-indexed #(vector %1 (Integer/parseInt %2)))
                 (reduce (fn [prev [index curr]]
                           (+ prev
                              (* curr (- 9 index))))
                           0))]
    (= 0 (mod sum 11))))

(s/fdef valid-account?
  :args (s/cat :account-number ::account-number)
  :ret boolean?)

(defn file->account-numbers
  "processes a `file` into a collection of account numbers"
  [file]
  (->> file
       partition-file
       (map line->cells)
       (map account-number-string)))

(s/fdef file->account-numbers
  :args (s/cat :file ::reader-type)
  :ret (s/or
        :account-numbers ::account-numbers
        :invalid ::empty-list))
