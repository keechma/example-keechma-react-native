(ns example-rn.domain.data)

(def checks
  [{:id 1
    :title "Item #1"
    :status "Received"
    :date "8/9/2018"
    :image (js/require "./images/unsplash/1.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :detail "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 8775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}
   {:id 2
    :title "Item #2" 
    :status "Not Received"
    :date "7/2/2018"
    :image (js/require "./images/unsplash/2.jpg")
    :details {:tin "7824000123"
              :time "12:21"
              :detail "Shift No93"
              :name "Alex McDowell"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 2775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}
                      {:id 4
                       :title "Adidas Yeezy"
                       :amount 2
                       :price 135}]}}
   {:id 3
    :title "Item #3"
    :status "Received"
    :date "6/24/2018"
    :image (js/require "./images/unsplash/3.jpg")
    :details {:tin "7824000321"
              :time "23:47"
              :detail "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 1775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}
   {:id 4
    :title "Item #4"
    :status "Correct"
    :date "5/21/20"
    :image (js/require "./images/unsplash/4.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :detail "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}
   {:id 5
    :title "Item #5"
    :status "Complaint"
    :date "5/19/20"
    :image (js/require "./images/unsplash/5.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :details "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 8775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}
   {:id 6
    :title "Item #6"
    :status "Not Received"
    :date "5/1/20"
    :image (js/require "./images/unsplash/6.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :detail "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 8775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}
   {:id 7
    :title "Item #7"
    :status "Received"
    :date "4/17/20"
    :image (js/require "./images/unsplash/7.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :detail "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 8775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}
   {:id 8
    :title "Item #8"
    :status "Received"
    :date "4/13/20"
    :image (js/require "./images/unsplash/8.jpg")
    :details {:tin "7824000336"
              :time "16:23"
              :detail "Shift No92"
              :name "Steven McCormick"
              :items [{:id 1
                       :title "Leather Moto Jacket \"Snake Strass\""
                       :amount 1
                       :price 8775}
                      {:id 2
                       :title "Hi-Top Sneakers \" Shiny Studs\""
                       :amount 1
                       :price 2410}
                      {:id 3
                       :title "Pochette \"Classy\""
                       :amount 1
                       :price 135}]}}])
