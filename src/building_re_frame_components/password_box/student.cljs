(ns building-re-frame-components.password-box.student
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as rf]))

(rf/reg-event-db
  :student/initialize
  (fn [_ _]
    {}))

(def password-validations
  (array-map
    "At least 12 characters."
    (comp (partial <= 12) count)

    "At least 50% unique characters."
    (fn [s]
      (-> s
          set
          count
          (/ (count s))
          (>= 0.5)))))

(defn validation-color [dirty? valid?]
  (when dirty?
    (if valid? "green" "red")))

(defn password-box []
  (let [s (reagent/atom {:value  ""
                         :valid? false})]
    (fn []
      (let [value (:value @s)
            validations (for [[desc f] password-validations]
                          [desc (f value)])
            valid? (every? identity (map second validations))
            dirty? (:dirty? @s)
            color (validation-color dirty? valid?)
            show? (:show? @s)]
        [:div
         [:label {:style {:color color}} "Password"]
         [:input {:type      (if show? :text :password)
                  :style     {:width  "100%"
                              :border (str "1px solid " color)}
                  :value     value
                  :on-focus  #(swap! s assoc :dirty? true)
                  :on-change #(swap! s assoc
                                :value (-> % .-target .-value))}]
         [:label [:input {:type      :checkbox
                          :checked   show?
                          :on-change #(swap! s assoc :show? (-> % .-target .-checked))}]
          " Show password?"]
         (for [[desc valid?] validations]
           (when dirty?
             [:div
              {:style {:color (validation-color dirty? valid?)}}
              (when dirty? (if valid? "✔ " "✘ ")) desc]))]))))

(defn ui []
  [password-box])

(when-some [el (js/document.getElementById "password-box--student")]
  (defonce _init (rf/dispatch-sync [:student/initialize]))
  (reagent/render [ui] el))

