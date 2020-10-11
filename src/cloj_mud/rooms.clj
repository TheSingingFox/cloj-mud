(ns cloj-mud.core
  (:gen-class))

(ns cloj-mud.rooms
  (:gen-class))

(load "objects")
(load "descriptions")

(defn room
  [name description objects]
    {:name name
     :descr description
     :obj objects})

(def hall (room :hall hall-description hall-objects))

(def room1 (room :room1 room1-description room1-objects))

(def room2 (room :room2 room2-description room2-objects))

(def room3 (room :room3 room3-description room3-objects))

(def main-map
  {:hall {:here hall
          :east room2
          :west room1
          :north room3}
   :room1 {:here room1
           :east hall}
   :room2 {:here room2
           :west hall}
   :room3 {:here room3
           :south hall}})
