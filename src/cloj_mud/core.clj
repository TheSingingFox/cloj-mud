(ns cloj-mud.core
  (:gen-class))

(require '[nrepl.server :refer [start-server stop-server]])
(defonce server (start-server :port 4001))

(defn make-player
  [name]
  (let [player {:name name
                :inventory #{}
                :location [0 0]}]
    (spit (str "./save/" name) (str "(atom " player ")"))))

(defn save-player
  [player]
  (spit (str "./save/" (:name @player)) (str "(atom " @player ")")))

(defn make-room
  [name location objects exits description]
  (let [room {:name name
              :location location
              :objects objects
              :exits exits
              :description description}]
    (spit (str "./rooms/" name) room)))

(defn make-object
  [name description]
  (let [obj {:name name
             :description description}]
    (spit (str "./objects/" name) obj)))

(defn get-state
  [save]
  (eval (read-string (slurp save))))

(defmacro change-loc
  [pl new-loc]
  `(swap! ~pl assoc :location ~new-loc))

(defn go
  [direction player]
  (let [loc (:location @player)]
    (cond (= direction 'west) (change-loc player (vector (loc 0) (inc (loc 1))))
          (= direction 'east) (change-loc player (vector (loc 0) (dec (loc 1))))
          (= direction 'north) (change-loc player (vector (inc (loc 0)) (loc 1)))
          (= direction 'south) (change-loc player (vector (dec (loc 0)) (loc 1))))))

(defn get-several
  [dir]
  (map get-state (rest (file-seq (clojure.java.io/file dir)))))

(defn get-players
  []
  (get-several "./save/"))

(defmacro get-one
  [source condition]
  `(loop [sq# ~source]
     (if (~condition sq#)
       (first sq#)
       (recur (rest sq#)))))

(defn get-player
  [name]
    (get-one (get-players) (fn [players] (= name (:name @(first players))))))

(defn objects
  []
  (get-several "./objects/"))

(defn get-obj-by-name [name]
     (get-one (objects) (fn [objs] (= name (:name (first objs))))))

(defn print-obj-descr
  [name]
  (println (:description (get-obj-by-name name))))

(defn print-objs-names
  [room]
  (doseq [x (map get-obj-by-name (map str (:objects room)))] (print (str ", " (:name x)))))

(defn rooms
  []
  (get-several "./rooms/"))

(defn rooms-map [] (atom (rooms)))

(defn get-loc-by-name [name]
    (get-one @(rooms-map) (fn [rooms] (= name (:name (first rooms))))))

(defn get-player-loc [player]
    (get-one @(rooms-map) (fn [rooms] (= (:location @player) (:location (first rooms))))))

(defn current-player
  [pl]
  (def player (get-player pl)))

(defn new-or-load
  []
  (println "Create a new character? (y/n)")
  (let [input (read-line)]
    (if (= input "y")
      (do (println "Give your new character a name: ")
          (let [name (read-line)]
            (make-player name)
            (current-player name)))
      (do (println "Load which character?")
          (current-player (read-line))))))

(defn make-room-in-game
  [player]
  (println "Where shall your new room be?")
  (let [location (let [direction (read-line)
                       loc (:location @player)]
                   (cond (= direction "west") (vector (loc 0) (inc (loc 1)))
                         (= direction "east") (vector (loc 0) (dec (loc 1)))
                         (= direction "north") (vector (inc (loc 0)) (loc 1))
                         (= direction "south") (vector (dec (loc 0)) (loc 1))))]
    (println "What shall your new room be called?")
    (let [name (read-line)]
      (println "Which exits does the new room have?")
      (let [exits (vector (read-line))]
        (println "Describe the new room.")
        (let [description (read-line)]
          (make-room name location #{} exits description))))))

(defn print-loc
  [player]
  (let [loc (get-player-loc player)]
  (println (:description loc))
  (print "Here are: ")
  (print-objs-names loc)
  (println)))

(defn to-invent
  [name player]
  (let [new-int (conj (:inventory @player)
                      (get-obj-by-name (str name)))]
  (swap! player assoc :inventory new-int)))

(defn print-invent
  [player]
  (doseq [x (map :name (:inventory @player))] (print (str ", " x)))
  (println))

(defmacro deed
  [action]
  `(do ~action
       (recur (read))))

(defn -main
  "Starts the game."
  [& args]
  (new-or-load)
  (println (str "Welcome " (:name @player) "!"))
  (print-loc player)
  (loop [input (read)]
    (cond (= input 'go) (deed (do (go (read) player)
                                  (print-loc player)))
          (= input 'look) (deed (print-loc player))
          (= input 'take) (deed (to-invent (read) player))
          (= input 'i) (deed (print-invent player))
          (= input 'save) (deed (save-player player))
          (= input 'read) (deed (print-obj-descr (str (read))))
          (= input 'repl) (deed (start-server :port 4001))
          (= input 'make-room) (deed (make-room-in-game player))
          (= input 'exit) (println "Goodbye!"))))
