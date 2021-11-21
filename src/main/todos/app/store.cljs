(ns todos.app.store
  (:require [re-frame.core :refer [reg-event-db reg-sub]]))

;; Helper functions

(defn filter-todos [todos completed]
  (filter #(= (:completed %) completed) todos))

(defn toggle-todo-completed [todos todo]
  (map (fn [current] (if (= (:id todo) (:id current)) (update current :completed #(not %)) current)) todos))

;; Event handlers

(reg-event-db
 :initialize
 (fn [_ _] {:todos [] :todo-message "" :show "all"}))

(reg-event-db
 :set-todo-message
 (fn [db [_ message]] (assoc db :todo-message message)))

(reg-event-db
 :add-todo
 (fn [db [_ message]]
   (update-in db [:todos] concat [{:id (random-uuid) :message message :completed false}])))

(reg-event-db
 :remove-todo
 (fn [db [_ todo]]
   (update-in db [:todos] (fn [todos] (remove #(= (:id %) (:id todo)) todos)))))

(reg-event-db
 :toggle-todo-completed
 (fn [db [_ todo]]
   (update-in db [:todos] #(toggle-todo-completed % todo))))

(reg-event-db
 :set-show
 (fn [db [_ show]]
   (assoc db :show show)))

(reg-event-db
 :clear-completed
 (fn [db [_]]
   (update db :todos #(filter-todos % false))))

;; Queries

(reg-sub
 :todos
 (fn [{todos :todos show :show} _]
   (cond
     (= show "completed") (filter-todos todos true)
     (= show "active") (filter-todos todos false)
     :else todos)))

(reg-sub :items-left-count (fn [{todos :todos} _] (count (filter-todos todos false))))

(reg-sub :items-completed-count (fn [{todos :todos} _] (count (filter-todos todos true))))

(reg-sub :todo-message (fn [db _] (:todo-message db)))

(reg-sub :show (fn [db _] (:show db)))
