(ns hello-cljs.components
  (:require
    [clojure.string :as str]
    [clojure.data :as data]
    [om.core :as om :include-macros true]
    [cljs.core.async :as async :refer [put! chan <!]]
    [sablono.core :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :as am :refer [go]]))

;; Misc views

(defn like-seymore [data owner]
  (reify
    om/IRender
    (render [_]
      (html
        [:div
         [:h1 "popular: " (:likes @data)]
         [:div [:a {:href "#"
                    ;; FIXME: Error: No protocol method ISwap.-swap!
                    ;; defined for type om.core/MapCursor: [object Object]
                    :onClick #(swap! data assoc :likes inc)}
                "Thumbs up"]]]))))

(defn my-widget [data owner]
  (reify
    om/IRender
    (render [_]
      (html
        [:h1 {:style {:color "blue"}} "Holloo"]))))

(defn stripe [text bgc]
  (let [st {:backgroundColor bgc}]
    [:li {:style st} text]))

(defn animals [data owner]
  (om/component
    (html
      [:ul
       ;; Map will pair the each color with an element from the list
       ;; until it works through the entire list.
       ;; https://clojuredocs.org/clojure.core/cycle#example-557b10f6e4b01ad59b65f4f2
       (map stripe (:list data) (cycle ["#ff0" "#f00"]))])))

;; Contacts View

;; TODO
;; print message when user has not inputed both first and last
;; clear input after entry

(defn parse-contact [contact-str]
  (let [[first middle last :as parts] (str/split contact-str #"\s+")
        [first last middle] (if (nil? last) [first middle] [first last middle])
        middle (when middle (str/replace middle "." ""))
        c (if middle (count middle) 0)]
    (when (>= (count parts) 2)
      (cond-> {:first first :last last}
        (== c 1) (assoc :middle-initial middle)
        (>= c 2) (assoc :middle middle)))))

(defn middle-name [{:keys [middle middle-initial]}]
  (cond
    middle (str " " middle)
    middle-initial (str " " middle-initial ".")))

(defn display-name [{:keys [first last] :as contact}]
  (str last "," first (middle-name contact)))

(defmulti entry-view (fn [person _] (:type person)))

(defmethod entry-view :student
  [person owner] (student-view person owner))

(defmethod entry-view :professor
  [person owner] (professor-view person owner))

(defn registry-view [data owner]
  (reify
    om/IRenderState
    (render-state [_ state]
      (html
        [:div
         [:h1 "Registry"]]))))

(defn contact-view [contact owner]
  (reify
    om/IRenderState
    (render-state [_ {:keys [delete]}]
      (html
        [:li
         [:span (display-name contact)]
         [:button {:style {:margin-left "1em"}
                   :on-click #(put! delete @contact)}"Delete"]]))))

(defn add-contact [data owner]
  (let [contact-input (om/get-node owner "new-contact")
        new-contact (parse-contact (.-value contact-input))]
    (when new-contact
      (om/transact! data :contacts #(conj % new-contact))
      (om/set-state! owner :text ""))))

(defn handle-change [e owner {:keys [text]}]
  (let [value (.. e -target -value)]
    (if-not (re-find #"[0-9]" value)
      (om/set-state! owner :text value)
      (om/set-state! owner :text text))))

(defn contacts-view [data owner]
  (reify
    om/IInitState
      (init-state [_]
        {:delete (chan)
         :text ""})
    om/IWillMount
    (will-mount [_]
      (let [delete (om/get-state owner :delete)]
        (go (loop []
          (let [contact (<! delete)]
            (om/transact! data :contacts
              ;; NOTE: vec is used to transform result of `remove`
              ;; which returns a lazy seq back into a vector
              (fn [xs] (vec (remove #(= contact %) xs))))
            (recur))))))
    om/IRenderState
    (render-state [_ {:keys [delete text] :as state}]
      (html
        [:div
          [:h1 "Contact List"]
          [:h4 (str "Count: " (count (:contacts @data)))]
          [:ul
            (om/build-all contact-view
                          (:contacts data)
                          {:init-state {:delete delete}})]
          [:div
           [:input {:type "text" :ref "new-contact" :value text
                    :on-change #(handle-change % owner state)}]
           [:button {:style {:margin-left "1em"}
                     :on-click #(add-contact data owner)} "Add contact"]]]))))


