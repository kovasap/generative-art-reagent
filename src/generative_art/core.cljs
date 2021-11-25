(ns generative-art.core
  (:require
    [quil.core :as q]
    [quil.middleware :as m]
    [reagent.core :as r]
    [reagent.dom :as d]))

(def background-color (r/atom 0))

(defn draw [{:keys [circles]}]
  (q/background @background-color)
  (doseq [{[x y] :pos [r g b] :color} circles]
    (q/fill r g b)
    (q/ellipse x y 10 10)))

(defn update-state [{:keys [width height] :as state}]
  (update state :circles conj {:pos   [(+ 20 (rand-int (- width 40)))
                                       (+ 20 (rand-int (- height 40)))]
                               :color (repeatedly 3 #(rand-int 250))}))

(defn init [width height]
  (fn []
    {:width   width
     :height  height
     :circles []}))

(defn canvas []
  (r/create-class
    {:component-did-mount
     (fn [component]
       (let [node (d/dom-node component)
             width (/ (.-innerWidth js/window) 2)
             height (/ (.-innerHeight js/window) 2)]
         (q/sketch
           :host node
           :draw draw
           :setup (init width height)
           :update update-state
           :size [width height]
           :middleware [m/fun-mode])))
     :render
     (fn [] [:div])}))


(defn background-color-slider [value]
  [:input {:type "range"
           :value value
           :min 0
           :max 255
           :style {:width "100%"}
           :on-change (fn [e]
                        (let [new-value (.. e -target -value)]
                          (reset! background-color  new-value)))}])

(defn home-page []
  (r/with-let [running? (r/atom false)]
    [:div
     [:h3 "circles demo"]
     [background-color-slider @background-color]
     [:div>button
      {:on-click #(swap! running? not)}
      (if @running? "stop" "start")]
     (when @running?
       [canvas])]))

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
