(ns todos.app.app
  (:require [re-frame.core :refer [dispatch subscribe]]
            [clojure.string :as string]))

;; Util components

(defn text-input [opts on-input]
  [:input.h-full.w-full.outline-none.text-gray-dark.text-xl.font-thin.placeholder-shown:text-gray-light.placeholder-shown:italic
   (assoc opts :on-change #(on-input (-> % .-target .-value)))])

;; Main components

(defn todos-header []
  [:h1.text-8xl.text-center.text-rose-200.font-thin.mt-10.mb-5 "todos"])

(defn toggle-all-button-on-click [evt completed]
  (.preventDefault evt)
  (dispatch [:set-all-completed completed]))

(defn toggle-all-button []
  (let [all-completed? @(subscribe [:all-completed?])
        class (if all-completed? "text-gray-dark" "text-gray-light")
        on-click #(toggle-all-button-on-click % (not all-completed?))]
    [:button.transform.rotate-90 {:class class :on-click on-click} "❯"]))

(defn todo-form-on-submit [evt message]
  (.preventDefault evt)
  (dispatch [:add-todo message])
  (dispatch [:set-todo-message ""]))

(defn todo-form []
  (let [message @(subscribe [:todo-message])
        on-submit #(todo-form-on-submit % message)
        on-input #(dispatch [:set-todo-message %])]
    [:form.h-full.w-full {:on-submit on-submit}
     [text-input {:type "text" :value message :placeholder "What needs to be done?"} on-input]]))

(defn todo-card-header []
  [:div.h-14.flex.border-b-2.border-solid.border-gray-200
   [:div.w-12.flex.justify-center
    (when @(subscribe [:any-todos?])
      [toggle-all-button])]
   [:div.flex-1
    [todo-form]]])

(defn todo-checkbox [todo]
  [:div.w-12.flex.justify-center.items-center
   [:input {:type "checkbox" :checked (:completed todo) :on-change #(dispatch [:toggle-todo-completed todo])}]])

(defn todo-text [todo]
  [:div.flex-1.my-auto.text-xl.font-thin
   (if (:completed todo)
     [:strike.text-gray-light (:message todo)]
     [:span.text-gray-dark (:message todo)])])

(defn remove-todo-button [todo]
  [:button.w-12.text-3xl.text-red-300.font-thin.hidden.group-hover:block
   {:on-click #(dispatch [:remove-todo todo])} "×"])

(defn todo-item [todo]
  [:div.h-14.flex.border-b.border-solid.border-gray-200.group
   [todo-checkbox todo]
   [todo-text todo]
   [remove-todo-button todo]])

(defn todo-list []
  (let [todos @(subscribe [:todos])]
    [:div
     (map (fn [todo] ^{:key (:id todo)} [todo-item todo]) todos)]))

(defn items-left-message []
  [:div.flex-1
   [:strong @(subscribe [:items-left-count])]
   " items left"])

(defn show-link [show]
  (let [active (= show @(subscribe [:show]))]
    [:span.mr-2.last:mr-0 {:class (when active "font-bold")}
     [:a {:on-click #(dispatch [:set-show show])} (string/capitalize show)]]))

(defn show-links []
  [:div
   [show-link "all"]
   [show-link "active"]
   [show-link "completed"]])

(defn clear-completed-button []
  [:div.flex-1.text-right
   (let [cnt @(subscribe [:items-completed-count])]
     (when (> cnt 0)
       [:button.font-thin {:type "button" :on-click #(dispatch [:clear-completed])} "Clear completed " cnt]))])

(defn todo-card-footer []
  [:div.flex.m-4.text-gray-dark.font-thin
   [items-left-message]
   [show-links]
   [clear-completed-button]])

(defn todos-card []
  [:div.bg-white.border.border-solid.border-gray-200.shadow
   [todo-card-header]
   [todo-list]
   (when @(subscribe [:any-todos?])
     [todo-card-footer])])

(defn app []
  [:div.max-w-screen-sm.mx-auto
   [todos-header]
   [todos-card]])
