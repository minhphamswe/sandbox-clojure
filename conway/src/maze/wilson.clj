(ns conway.core
  (:gen-class))

;; Wilson's maze generation algorithm:
;; 1. Randomly pick a location & mark as "visited".
;; 2. Randomly pick a location that isn't visited yet. If there's none, return
;; the maze.
;; 3. Perform random walk starting from newly-picked location until stumble on
;; a location that is "visited". If you pass through a location more than once
;; during the random walk, always remember the direction you take to leave it.
;; 4. Mark all the locations of the random walk as "visited", and remove walls
;; according to the last exit direction.
;; 5. Repeat from 2.

(defn maze
  "Return a maze carved out of walls.

  Params:
    walls is a set of 2-item sets #{a b} where a and b are locations.

   The returned maze is a set of the remaining walls"
  [walls]
  (let [paths (reduce (fn [index [a b]]
                        (merge-with into index {a [b], b [a]}))
                      {}
                      (map seq walls))
        start-loc (rand-nth (keys paths))]
    ;; paths: maps from locations to adjacent locations
    (loop [walls walls
           unvisited (disj (set (keys paths)) start-loc)]
      (if-let [loc (when-let [s (seq unvisited)] (rand-nth s))]
        (let [walk (iterate (comp rand-nth paths) loc)
              steps (zipmap (take-while unvisited walk) (next walk))]
          (recur (reduce disj walls (map set steps))
                 (reduce disj unvisited (keys steps))))
        walls))))

(defn grid
  [w h]
  (set (concat
         (for [i (range (dec w)) j (range h)] #{[i j] [(inc i) j]})
         (for [i (range w) j (range (dec h))] #{[i j] [i (inc j)]}))))

(defn draw
  [w h maze]
  (doto (javax.swing.JFrame. "Maze")
    (.setContentPane
      (doto (proxy [javax.swing.JPanel] []
              (paintComponent [^java.awt.Graphics g]
                (let [g (doto ^java.awt.Graphics2D (.create g)
                          (.scale 10 10)
                          (.translate 1.5 1.5)
                          (.setStroke (java.awt.BasicStroke. 0.4)))]
                  (.drawRect g -1 -1 w h)
                  (doseq [[[xa ya] [xb yb]] (map sort maze)]
                    (let [[xc yc] (if (= xa xb)
                                    [(dec xa) ya]
                                    [xa (dec ya)])]
                      (.drawLine g xa ya xc yc))))))
        (.setPreferredSize (java.awt.Dimension.
                             (* 10 (inc w)) (* 10 (inc h))))))
    (.pack)
    (.setVisible true)))

;(maze (grid 40 40))
(draw 40 40 (maze (grid 40 40)))