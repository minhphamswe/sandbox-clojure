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

(def glider #{[2 0] [2 1] [2 2] [1 2] [0 1]})

(defn neighbors
  "Get the indices of the cells next to a cell."
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

;; The state of the world can be entirely represented by the set of living
;; cells. Cells that are not living are implied dead. To generate each
;; successive state, count how many living neighbors (n) each cell has.
;; - n = 2 => same state
;; - n = 3 => turn on
;; - else  => turn off/die
(defn step
  "Yied the next state of the world."
  [cells]
  (set (for [[loc n] (frequencies (mapcat neighbors cells))
             :when (or (= n 3) (and (= n 2) (cells loc)))]
         loc)))

(defn -main
  [& args]
  (pprint glider)

  ;(pprint (populate (empty-board 6 6) (first (drop 8 (iterate step glider)))))
  (->>
    (iterate step glider)
    (drop 8)
    first
    (populate (empty-board 6 6))
    pprint))
