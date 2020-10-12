(ns cloj-mud.core
  (:gen-class))

(require '[nrepl.server :refer [start-server stop-server]])
(defonce server (start-server :port 4001))

(load "objects")
(load "descriptions")
(load "rooms")
(load "navigation")

(def start (:hall cloj-mud.rooms/main-map))

(def inventory #{})

(defn look [loc]
  (println (:descr (:here (cloj-mud.navigate/here loc))))
  (println (str "You see "
                (if (= (:obj (:here (cloj-mud.navigate/here loc))) #{})
                  "nothing."
                  (str "a " (clojure.string/join ", a " (:names (:obj (:here (cloj-mud.navigate/here loc))))))))))

(defn take-invent
  [invent]
  (println (str "You carry "
                (if (= invent #{})
                  "nothing."
                  (str "a " (clojure.string/join ", a " (map :name invent)))))))

(defn take-obj
  [loc obj]
  (obj (:obj (:here (cloj-mud.navigate/here loc)))))

(defn add-to-inventory
  [loc obj invent]
  (conj invent (get (:obj (:here (cloj-mud.navigate/here loc))) obj)))

(defn go
  [loc dir]
  (cond (= dir 'north) (cloj-mud.navigate/north loc)
        (= dir 'south) (cloj-mud.navigate/south loc)
        (= dir 'west) (cloj-mud.navigate/west loc)
        (= dir 'east) (cloj-mud.navigate/east loc)
        :else (cloj-mud.navigate/here loc)))


(defn run
  "Move from one room to the other."
  []
  (loop [loc (:hall cloj-mud.rooms/main-map)
         invent inventory
         input (read)]
    (cond (= input 'look) (do (look loc)
                              (recur (cloj-mud.navigate/here loc)
                                     invent
                                     (read)))
          (= input 'go) (do (let [dir (read)]
                              (look (go loc dir))
                              (recur (go loc dir)
                                     invent
                                     (read))))
          (= input 'take) (do (let [obj (read)]
                                (println (str "You take up the " (:name (take-obj loc obj))))
                                (recur (cloj-mud.navigate/here loc)
                                       (add-to-inventory loc obj invent)
                                       (read))))
          (= input 'i) (do (take-invent invent)
                           (recur (cloj-mud.navigate/here loc)
                                  invent
                                  (read)))
          (= input 'read) (do (let [obj (read)]
                                (println (:descr (take-obj loc obj)))
                                (recur (cloj-mud.navigate/here loc)
                                       invent
                                       (read))))
          (= input 'exit) (println "Goodbye!")
          :else (do (println "You cannot do this!")
                    (recur (cloj-mud.navigate/here loc)
                           invent
                           (read)))
          )))

(defn -main
  "Starts the game."
  [& args]
  (start-server)
  (look start)
  (run))
