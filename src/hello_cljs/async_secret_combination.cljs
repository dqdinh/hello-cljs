;; http://www.jayway.com/2014/09/16/comparing-core-async-and-rx-by-example/

(ns hello-cljs.async-secret-combination
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async
             :refer [>! <! put! chan alts!]]
            [goog.events :as events]
            [goog.dom.classes :as classes])
  (:import [goog.events EventType]))


(enable-console-print!)

;; =============================================================================
;; Utilities

(defn by-id [id]
  "short hand for document.getElementById(id)"
  (.getElementById js/document id))

(defn events->chan
  "Given a target DOM element and event type return a channel of
  observed events. Can supply the channel to receive events as third
  optional argument"
  ([el event-type] (events->chan el event-type (chan)))
  ([el event-type ch]
   (events/listen el event-type (fn [e] (put! ch e)))
   ch))

(defn set-html!
  "Given a CSS id, replace the matching DOM element's
  content with the supplied string."
  [id s]
  ;; core functions by-id and by-class which return a DomContent based on node id and node class
  (set! (.-innerHTML (by-id id)) s))

;; =============================================================================
;; Clojure Channel Example

(defn ex1 []
  (let [a (events->chan (by-id "ex1-button-a") EventType.CLICK (chan 1 (map (constantly :a))))
        b (events->chan (by-id "ex1-button-b") EventType.CLICK (chan 1 (map (constantly :b))))
        combination-max-time 5000
        secret-combination [:a :b :b :a :b :a]
        set-html! (partial set-html! "ex1-card")]
    ;; go is a Clojure macro that takes the body and enable usage of channels inside it.
    ;; Under the hood the body turns into a state machine. When reaching a blocking channel operation
    ;; the thread will be released and the state machine parked.
    ;; This gives us the illusion of writing sequential code in an asynchronous environment.
    (go
      (loop [correct-clicks []
             ;; returns a chan that closes after specified time.
             timeout (async/timeout combination-max-time)]
        ;; the alts! function will “block” at this line until
        ;; data is available in any of these channels.
        (let [[val channel] (alts! [a b timeout])
              clicks (conj correct-clicks val)]
          (cond
            (= channel timeout)
            (do (set-html! "Not fast enough. Must be under 5 secs")
                (recur [] (async/timeout combination-max-time)))

            (= clicks secret-combination)
            (do (set-html! "Achievement unlocked!")
                (recur [] (async/timeout combination-max-time)))

            ;; ;; NOTE: I think this is here to give the user an extra 5 secs after the first
            ;; ;; correct click. Safe to remove.
            ;; (and (= val (first secret-combination)) (zero? (count correct-clicks)))
            ;; (do (set-html! clicks)
            ;;     (recur clicks (async/timeout combination-max-time)))

            (= val (nth secret-combination (count correct-clicks)))
            (do (set-html! clicks)
                (recur clicks timeout))
            :else
            ;;handle the case when a button click doesn’t match the secret combination
            (do (set-html! clicks)
                (recur [] timeout))))))))

(ex1)


