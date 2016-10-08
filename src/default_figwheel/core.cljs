(ns default-figwheel.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(enable-console-print!)

(println "This text is printed from src/default-figwheel/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))


(defn setup []
  (q/frame-rate 50)
  (q/color-mode :hsb)
  (q/background 60)

  {:color 0
   :angle 0})

(defn update-state [state]
  {:color      (mod (* (/ (q/frame-count)
                         1000)
                      255)
                 255)
   :angle      (+ (:angle state) (/ 0.25 2))
   :ctrl-point (let [bound #(q/constrain % -3 3)
                     easing 0.03
                     prev-x (get-in state [:ctrl-point :x])
                     prev-y (get-in state [:ctrl-point :y])
                     x-dist (- (q/mouse-x) prev-x)
                     y-dist (- (q/mouse-y) prev-y)]
                 {:x (+ prev-x (bound (* x-dist easing))),
                  :y (+ prev-y (bound (* y-dist easing)))})})

(defn f [t]
  [(* (* t t) (q/sin t))
   (* (* t t) (q/cos t))])

(defn stagger-plot
  [f from to step increment]
  (doseq [two-points (->> (range from to step)
                       (mapcat #(list % (+ % increment)))
                       (map f)
                       (partition 2 1))]
    (apply q/line two-points)))

(defn draw [state]
  (let [color (:color state)
        ctrl-y (get-in state [:ctrl-point :y])
        ctrl-x (get-in state [:ctrl-point :x])
        angle (:angle state)]
    (q/background 24)
    (q/stroke color 255 255 200)
    (q/with-translation [ctrl-x ctrl-y]
      (stagger-plot f 0 40
        (+ 0.05
          (* 0.5
            (/ ctrl-y
              (q/height))))
        (+ q/PI
          (* q/TWO-PI
            (/ ctrl-x
              (q/width))))))
    (q/no-stroke)
    (q/fill color 255 255 100)
    (q/rect-mode :center)
    (let [draw-tri (fn []
                     (apply q/triangle
                       (map (partial *
                              (* 5 (+ 1 (* 0.2 (q/sin (/ angle 6))))))
                         [0 2 (q/sqrt 3) -1 (- (q/sqrt 3)) -1])))
          draw-fn (fn [k]
                    (q/with-rotation [(* k angle)]
                      (draw-tri)))]
      (q/with-translation [ctrl-x ctrl-y]
        (draw-fn 1)
        (draw-fn -1)
        (draw-fn (q/sqrt 2))))))

(q/defsketch default-figwheel
  :host "default-figwheel"
  :size [1000 1000]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])













