(ns cloj-mud.core
  (:gen-class))

(ns cloj-mud.navigate
  (:gen-class))

(defmacro go
  [loc
   input]
  `((:name (~input ~loc)) cloj-mud.rooms/main-map))

(defn west
  "Move one room to the west."
  [loc]
  (go loc :west))

(defn east
  "Move one room to the east."
  [loc]
  (go loc :east))

(defn north
  "Move one room to the north."
  [loc]
  (go loc :north))

(defn south
  "Move one room to the south."
  [loc]
  (go loc :south))

(defn here
  "Returns the current room."
  [loc]
  (go loc :here))
