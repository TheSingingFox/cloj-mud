(ns cloj-mud.core
  (:gen-class))

(require '[nrepl.server :refer [start-server stop-server]])
(defonce server (start-server :port 4001))

(load "objects")
(load "descriptions")
(load "rooms")
(load "navigation")

(def delim "========================================================================================")

(def greeting
  "\"Welcome, Cloj-Mud, to this humble abode! We are glad to receive you here as our guest!\"
The wise, bearded old man greets you. You wonder how he knows your name. He speaks 
again: 
\"You wonder how I know your name? I know many things. You see, I am a wise, bearded 
old man. Not exactly a wizard, and my name is not Gandalf, but let's say I have a direct 
connection to the maker of this world - unlike other people you might meet here.\"")

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
          (= input 'east) (do (look (cloj-mud.navigate/east loc))
                              (recur (cloj-mud.navigate/east loc)
                                     invent
                                     (read)))
          (= input 'west) (do (look (cloj-mud.navigate/west loc))
                              (recur (cloj-mud.navigate/west loc)
                                     invent
                                     (read)))
          (= input 'north) (do (look (cloj-mud.navigate/north loc))
                               (recur (cloj-mud.navigate/north loc)
                                      invent
                                      (read)))
          (= input 'south) (do (look (cloj-mud.navigate/south loc))
                               (recur (cloj-mud.navigate/south loc)
                                      invent
                                      (read)))
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
          :else (println "You cannot do this!")
          )))

(defn -main
  "Starts the game."
  [& args]
  (start-server)
  (look start)
  (run))

