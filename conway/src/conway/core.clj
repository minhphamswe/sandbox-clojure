(ns conway.core
  (:gen-class)
  (:require [clojure.pprint :refer [pprint]]))

(defn empty-board
  "Create a rectangular empty board of a certain width and height."
  [w h]
  (vec (repeat w (vec (repeat h nil)))))

(defn populate
  "Turn :on each of the cells specified as [y x] coordinates."
  [board living-cells]
  (reduce (fn [board coordinates]
            (assoc-in board coordinates :on))
          board
          living-cells))

(def glider
  (populate (empty-board 6 6)
            #{[2 0] [2 1] [2 2] [1 2] [0 1]}))

(defn window
  "Get a lazy sequence of 3-item windows centered around each item of coll."
  ([coll]
   (window nil coll))
  ([pad coll]
   (partition 3 1 (concat [pad] coll [pad]))))

(defn cell-block
  "Create sequence of 3x3 windows from triple of 3 sequences."
  [[left mid right]]
  (window (map vector left mid right)))

(defn liveness
  "Get the liveness (nil or :on) of the center cell for the next step."
  [block]
  (let [[_ [_ center _] _] block]
    (case (- (count (filter #{:on} (apply concat block)))
             (if (= :on center) 1 0))
      2 center
      3 :on
      nil)))

(defn- step-row
  "Given three rows, yield the next state of the center row."
  [rows-triple]
  (vec (map liveness (cell-block rows-triple))))

(defn step
  "Yield the next state of the board."
  [board]
  (vec (map step-row (window (repeat nil) board))))

(defn -main
  [& args]
  (pprint glider)
  (->
    (iterate step glider)
    (nth 8)
    pprint))
