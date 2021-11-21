(ns todos.app.main
  (:require [reagent.dom :as rdom]
            [re-frame.core :refer [dispatch-sync]]
            [todos.app.app :refer [app]]
            [todos.app.store]))

(defn ^:dev/after-load render []
  (rdom/render [app] (js/document.getElementById "app")))

(defn init []
  (dispatch-sync [:initialize])
  (render))