(ns app.logic.generator
  (:require [app.utils :refer :all]))

(declare gen-sections-prompt gen-content-prompt)

(defn- get-result
  "a helper function to get the result from the generator response
  returns the value of the :result key in the response map"
  [result]
  (get-in result [:result]))

(defn gen-sections
  "Generate sections of an article"
  [openai {:keys [n-sections title prompt]}]
  (let [gen-fn (:openai openai)
        result (-> (gen-fn {:model    :gpt-3
                            :messages (gen-sections-prompt
                                        {:n-sections n-sections
                                         :title      title
                                         :prompt     prompt})})
                   (try (catch Exception e
                          (pres e)))
                   let-pres)]
    (get-result result)))

(defn gen-content
  "Generate content of a section"
  [openai article-data {:keys [title description model]}]
  (let [gen-fn (:openai openai)]
    (let-pres (try (gen-fn {:model    model
                            :messages (gen-content-prompt
                                        {:title          title
                                         :description    description
                                         :article-title  (:title article-data)
                                         :article-prompt (:prompt article-data)})})
                   (catch Exception e (pres e))))))

(defn gen-content-prompt
  "Generate prompts for content"
  [{:keys [title description article-title article-prompt]}]
  [{:role    "system"
    :content "I'm a very good writer that can help you write enlightening articles that help people unlearn their wrong beliefs and learn new insights to see the world according to scientific knowledge and use good logical thinking"}
   {:role    "user"
    :content (str "Gue lagi mau nulis buku pendek tentang " article-title " yang covering " article-prompt)}
   {:role    "assistant"
    :content "Ok, gue bantu di bagian mana?"}
   {:role    "user"
    :content (str "Gue mau elo tulis article yang judulnya ini " title " dengan isi yang mengcover ini " description)}
   {:role    "assistant"
    :content "Terus ini mesti gue apain lagi?"}
   {:role    "user"
    :content "Gue pengen elo tulis artikel tadi jadi article panjang minimal 1500 kata, dan kalo perlu ada contoh2xnya, terus kalo butuh code juga kasih yg banyak, pokoknya sampe yg baca beneran bisa ngerti or nguasain yg gue tulis di atas"}
   {:role    "assistant"
    :content "Ok, bahasanya gimana?"}
   {:role    "user"
    :content "Gue mau bahasanya in bahasa indonesia campur sedikit english yg informal, santai, witty, smart, and humorous tapi jangan cringe"}
   {:role    "assistant"
    :content "Ok, formatnya gimana?"}
   {:role    "user"
    :content "Formatnya json, propertynya article-title, article-content, dan article-language (either \"english\", \"indo\", or \"campur\",
      di article-content elo tulis article yg elo bikin dan elaborasi jadi panjang dengan format html, kalo elo kasih
      contoh coding elo kasih format code block di html-nya yak"}])

(defn gen-sections-prompt
  "Generate prompts for sections"
  [{:keys [title n-sections prompt]}]
  [{:role    "system"
    :content "I'm a very good writer that can help you write enlightening articles that help people unlearn their wrong beliefs and learn new insights to see the world according to scientific knowledge and use good logical thinking"}
   {:role    "user"
    :content (str "Ok, gue mau nulis article panjang yg dipecah jadi beberapa section tentang ini " title)}
   {:role    "assistant"
    :content "Ok, gue bantu di bagian mana?"}
   {:role    "user"
    :content (str "Gue mau elo pecah penjelasannya dalam " n-sections " section. Tiap section isinya ada :title & :description.
                   Gue mau elo kasih beberapa ide cara mecah article ini beberapa sections, tiap section isinya ada :title & :description.")}
   {:role    "assistant"
    :content "Ok, detailnya elo pengen gue jelasin bagian apa?"}
   {:role    "user"
    :content (str "Gue mau elo jelasin ini " prompt)}
   {:role    "assistant"
    :content "Ok, bahasanya gimana?"}
   {:role    "user"
    :content "Gue mau bahasanya in Bahasa Indo campur sedikit english yg informal, santai, witty, smart, and humorous tapi jangan cringe"}
   {:role    "assistant"
    :content "Ok, formatnya gimana?"}
   {:role    "user"
    :content "Formatnya json, list of sections, tiap section ada property title & description, isi description basically cuma list of topics yang mesti dicover aja,
    ga usah pake penjelasan, semacam outline aja, tapi string aja ya formatnya jangan array, list yang banyak topiknya"}])