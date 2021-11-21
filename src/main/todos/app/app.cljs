(ns todos.app.app
  (:require [re-frame.core :refer [dispatch subscribe]]
            [clojure.string :as string]))

(defn todos-header []
  [:h1 "Todos"])

(defn todo-form-on-submit [evt message]
  (.preventDefault evt)
  (dispatch [:add-todo message])
  (dispatch [:set-todo-message ""]))

(defn todo-form []
  (let [message @(subscribe [:todo-message])]
    [:form {:on-submit #(todo-form-on-submit %1 message)}
     [:input {:type "text" :value message :on-change #(dispatch [:set-todo-message (-> % .-target .-value)])}]]))

(defn todo-item-remove-on-click [evt todo]
  (.preventDefault evt)
  (dispatch [:remove-todo todo]))

(defn todo-item [todo]
  [:div
   [:input {:type "checkbox" :checked (:completed todo) :on-change #(dispatch [:toggle-todo-completed todo])}]
   " "
   (let [tag (if (:completed todo) :strike :span)]
     [tag (:message todo)])
   " "
   [:a {:href "" :on-click #(todo-item-remove-on-click %1 todo)} "X"]])

(defn todo-list []
  (let [todos @(subscribe [:todos])]
    [:div
     (map (fn [todo] ^{:key (:id todo)} [todo-item todo]) todos)]))

(defn items-left-message []
  [:span
   [:strong @(subscribe [:items-left-count])]
   " items left"])

(defn set-show-on-click [evt show]
  (.preventDefault evt)
  (dispatch [:set-show show]))

(defn show-link [show]
  (let [link [:a {:href "" :on-click #(set-show-on-click % show)} (string/capitalize show)]]
    (if (= show @(subscribe [:show])) [:strong link] link)))

(defn show-links []
  [:div
   [show-link "all"]
   " "
   [show-link "active"]
   " "
   [show-link "completed"]])

(defn clear-completed-button []
  (let [cnt @(subscribe [:items-completed-count])]
    (when (> cnt 0)
      [:button {:type "button" :on-click #(dispatch [:clear-completed])} "Clear completed " cnt])))

(defn app []
  [:<>
   [todos-header]
   [todo-form]
   [todo-list]
   [items-left-message]
   [show-links]
   [clear-completed-button]])
