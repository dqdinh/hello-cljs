(ns hello-cljs.utils
   :require [cljs.core.async :as async
             :refer [>! <! put! chan alts!]])

(defn middle-name [{:keys [middle middle-initial]}]
  (cond
    middle (str " " middle)
    middle-initial (str " " middle-initial ".")))

(defn display-name [{:keys [first last] :as contact}]
  (str last "," first (middle-name contact)))

(def remote-data (vec (map #(str "Item " %) (range 100))))

(defmulti serve :op)

(defmethod serve :data
  [{:keys [start per-page res]}]
  (put! res (subvec remote-data start per-page)))
