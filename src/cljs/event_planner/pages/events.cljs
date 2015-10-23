(ns event-planner.pages.events)

(defn events-page [id]
  [:div.container
    [:div.row.col-xs-4
      [:form.form-signin
        [:h2.form-signin-heading "Sign In"]
        [:label {:for "inputusername"
                 :class "sr-only"} "Username"]
        [:input {:type "text"
                 :id "inputusername"
                 :placeholder "Username"
                 :required
                 :autofocus}]
        [:label {:for "inputpassword"
                 :class "sr-only"} "Password"]
        [:input {:type "password"
                 :id "inputpassword"
                 :placeholder "Password"}]
        [:button.btn.btn-sm.btn-primary {:type "Submit"} "Sign In"]]
      [:p ]
      [:p "Please note that all sign in usernames and passwords are temporary."]
      [:p "To sign in using your account please go to the "
        [:a {:href "#"} "Login"] " page."]]
    [:div.row.col-xs-8
     [:h1 "HELL YEAH"]]])
