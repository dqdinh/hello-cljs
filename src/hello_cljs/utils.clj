(ns ^:figwheel-always hello-cljs.utils)

(defmacro logger [body]
  `(.log js/console (prn-str ~@body)))
