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

;; The state of the world can be entirely represented by the set of living
;; cells. Cells that are not living are implied dead. To generate each
;; successive state, count how many living neighbors (n) each cell has.
(defn stepper
  "Return a stepper function for Life-like cell automata.

  Params:
    neighbors(fn) takes a location & returns sequential collection of locations.
    survive?(fn) and birth?(fn) are predicates on the number of living
      neighbors.
  "
  [neighbors birth? survive?]
  (fn [cells]
    (set (for [[loc n] (frequencies (mapcat neighbors cells))
               :when (if (cells loc) (survive? n) (birth? n))]
           loc))))

(defn neighbors
  "Get the indices of the cells next to a cell."
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))


(defn -main
  [& args]
  (pprint glider)

  ;(pprint (populate (empty-board 6 6) (first (drop 8 (iterate step glider)))))
  (->>
    (iterate (stepper neighbors #{3} #{2 3}) glider)
    (drop 8)
    first
    (populate (empty-board 6 6))
    pprint))
