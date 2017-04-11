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

(defn neighbors
  "Get the indices of the cells next to a cell."
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(defn count-neighbors
  "Count number of neighbors to a cell that is not nil."
  [board loc]
  (count (filter #(get-in board %) (neighbors loc))))

(defn indexed-step
  "Yield the next state of the board, using indices to determine neighbors."
  [board]
  (let [w (count board)
        h (count (first board))]
    (loop [new-board board
           x 0
           y 0]
      (cond
        (>= x w) new-board
        (>= y h) (recur new-board (inc x) 0)
        :else
        (let [new-liveness
              (case (count-neighbors board [x y])
                2 (get-in board [x y])
                3 :on
                nil)]
          (recur (assoc-in new-board [x y] new-liveness) x (inc y)))))))

(defn -main
  [& args]
  (pprint glider)
  (->
    (iterate indexed-step glider)
    (nth 8)
    pprint))
