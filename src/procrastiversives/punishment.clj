(ns procrastiversives.punishment
  (:require [taoensso.timbre :as log]))

(defn ^:private take-while-inclusive [elem col]
  (concat (take-while (partial (comp not =) elem) col)
          (filter (partial = elem) col)))

(def ^:private levels '(:mild
              :scary
              :extreme
              :goatse))

(defn ^:private categories-in [level]
  (take-while-inclusive level levels))


(def bad-links {:mild
                  '("http://www.memecenter.com/fun/1102511/spider-kitty-scary-kitty-six-eyes-upon-its-head-bitey-kitty-venom-kitty-pet-it-and-youre-dead"
                    "http://upload.wikimedia.org/wikipedia/commons/2/2d/Tarantula_020.jpg")
                  :scary '("http://scaryassshit.com/wp-content/uploads/2013/04/Spiral-flower-bloom-wallpaper-scary-face-wallpaper.jpg"
                           "https://www.google.co.uk/search?q=scary+images&espv=210&es_sm=91&source=lnms&tbm=isch&sa=X&ei=LMDAUta2OYyDkQfS7IDYCg&ved=0CAkQ_AUoAQ&biw=1279&bih=702#es_sm=91&espv=210&q=scary+images&tbm=isch&tbs=itp:photo,isz:l&facrc=_&imgdii=_&imgrc=hbSRm6UsBtq22M%3A%3BsywSw5FORQ90rM%3Bhttp%253A%252F%252Fwp.1920x1080.org%252Fwp-content%252Fuploads%252F2011%252F11%252Fscary-wallpapers-for-desktop-352830468.jpg%3Bhttp%253A%252F%252Fwp.1920x1080.org%252F1920x1080-scary-wallpapers-for-desktop%252Fscary-wallpapers-for-desktop-352830468-jpg%252F%3B1920%3B1080"
                           "http://creepypasta.wikia.com/wiki/Creepypasta_Wiki:Monsters_(Gallery)/Page_1?file=1309089602949.jpg")
                  :extreme '("https://www.google.com/search?as_st=y&tbm=isch&as_q=tarantula&as_epq=&as_oq=&as_eq=&cr=&as_sitesearch=&safe=images&tbs=isz:l,sur:f#as_st=y&q=Trypophobia&tbas=0&tbm=isch&imgdii=_"
                             "http://regretfulmorning.com/2011/04/scary-gifs/?pid=879")
                  :goatse '("http://www.goatse.fr/"
                            "http://www.lemonparty.tv/"
                            ;; Oh my god, no more
                            )})

(defn pick-punishment [level]
  (->> (categories-in (keyword level))
      (map bad-links)
      (flatten)
      (rand-nth)))
