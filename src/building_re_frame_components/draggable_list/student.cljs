(ns building-re-frame-components.draggable-list.student
  (:require
    [re-frame.core :as rf]
    [reagent.core :as reagent]))

(rf/reg-event-db
  :student/initialize
  (fn [_ _]
    {}))

(defn- put-before [items pos item]
  (let [items (remove #{item} items)
        [head tail] (split-at pos items)]
    (concat head [item] tail)))

(defn draggable-list [{:keys [on-reorder]
                       :or   {on-reorder (fn [])}} & items]
  (let [items (vec items)
        s (reagent/atom {:order (range (count items))})]
    (fn []
      (let [{:keys [order drag-index]} @s]
        [:ul
         (for [[pos i] (map-indexed vector order)]
          [:li
           {:key           i
            :draggable     true
            :on-drag-start #(swap! s assoc :drag-index i)
            :on-drag-over  (fn [e]
                             (.preventDefault e)
                             (swap! s assoc :drag-over pos)
                             (swap! s update :order put-before pos drag-index))
            :on-drag-leave #(swap! s assoc :drag-over :nothing)
            :on-drag-end   (fn []
                             (swap! s dissoc :drag-over :drag-index)
                             (on-reorder (mapv items order)))}
           (when-not (= i drag-index)
             (get items i))])]))))

(defn ui []
  (let [s (reagent/atom {})]
    (fn []
      [:div
       (pr-str (:order @s))
       [draggable-list
        {:on-reorder (fn [item-order]
                       (swap! s assoc :order item-order))}
        "a"
        "b"
        "c"
        "d"]])))

(when-some [el (js/document.getElementById "draggable-list--student")]
  (defonce _init (rf/dispatch-sync [:student/initialize]))
  (reagent/render [ui] el))
