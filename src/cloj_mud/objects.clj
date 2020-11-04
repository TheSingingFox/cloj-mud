(ns cloj-mud.rooms
  (:gen-class))

(def sign {:name 'sign
           :descr "The sign reads:
     ____________________
     |                  |
     | \"Hello, Player!\" |
     |__________________|"})

(def guestbook {:name 'guestbook
                :descr "A guestbook."})

(def broom {:name 'broom
            :descr "A broom."})

(def bucket {:name 'bucket
             :descr "A bucket."})

(def rooms-map {:name 'rooms-map
          :descr "
                   +---------------------+
                   |                     |
                   |                     |
                   |       Bedroom       |
                   |                     |
                   |                     |
                   |                     |
               /---+---------¦ ¦---------+---\\
              /                               \\
             /                                 \\
  +---------+                                   +---------+
  |         |                                   |         |
  |         |                                   |         |
  |         +                                   + Living  |
  | Cupboard           Entrance Hall                      |
  |         +                                   +  Room   |
  |         |                                   |         |
  |         |                                   |         |
  +---------+                                   +---------+
            \\                                   /
             \\                                 /
              \\-------------------------------/
"})

(def objects
  {:guestbook guestbook
   :broom broom
   :sign sign
   :bucket bucket
   :map rooms-map})

(def hall-objects {:names #{'guestbook 'map}
                   'guestbook (:guestbook objects)
                   'map (:map objects)})

(def room1-objects {:names #{'broom 'bucket}
                    'broom (:broom objects)
                    'bucket (:bucket objects)})

(def room2-objects #{})

(def room3-objects {:names #{'sign}
                    'sign (:sign objects)})
