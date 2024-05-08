(ns app.logic.generator
  (:require [app.utils :refer :all]))

(declare gen-sections-prompt gen-content-prompt content-prompt-extender gen-proset-prompt)

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

(defn gen-content-extender
  "Generate prompt for extended content of a section"
  [openai {:keys [article-title section-title section-description]}]
  (let [gen-fn (:openai openai)
        result (-> (gen-fn {:model    :gpt-3
                            :messages (content-prompt-extender
                                        {:article-title        article-title
                                         :section-title        section-title
                                         :section-description  section-description})})
                   (try (catch Exception e
                          (pres e)))
                   let-pres)]
    (get-result result)))

(defn gen-content
  "Generate content of a section"
  [openai {:keys [article-title title extended-description]}]
  (let [gen-fn (:openai openai)
        result (-> (gen-fn {:model    :gpt-3
                            :messages (gen-content-prompt
                                        {:article-title        article-title
                                         :section-title        title
                                         :section-description  extended-description})})
                   (try (catch Exception e
                          (pres e)))
                   let-pres)]
    (assoc (get-result result) :description extended-description)))

(defn gen-proset
  "Generate proset"
  [openai {:keys [title content]}]
  (let [gen-fn (:openai openai)
        result (-> (gen-fn {:model    :gpt-3
                            :messages (gen-proset-prompt
                                        {:title    title
                                         :content  content})})
                   (try (catch Exception e
                          (pres e)))
                   let-pres)]
    (get-result result)))

(defn gen-content-prompt
  "Generate content from content extender. using gpt3"
  [{:keys [article-title section-title section-description]}]
  [{:role    "system"
    :content "I'm a very good writer that can help you write enlightening articles that help people unlearn their wrong beliefs and learn new insights
              to see the world according to scientific knowledge and use good logical thinking"}
   {:role    "user"
    :content (str "Gue lagi ngarang article judulnya '" article-title "'. "
                  "Sekarang gue lagi nulis satu section dari buku itu, yaitu '" section-title "'.")}
   {:role    "assistant"
    :content "Ok, gue bantu di bagian mana?"}
   {:role    "user"
    :content (str "Gue pengen elo tulis section tadi jadi article panjang minimal 1200 kata yang mengcover ini: " section-description
                  ". dan kalo perlu ada contoh2xnya, pokoknya sampe yg baca beneran bisa ngerti or nguasain yg gue tulis di atas.")}
   {:role    "assistant"
    :content "Ok, bahasanya gimana?"}
   {:role    "user"
    :content "Gue mau bahasanya bahasa Indonesia yang nyampur2 dengan bahasa English, kyk Jaksel gitu yg informal, santai, witty, smart, and humorous tapi jangan cringe.
    Terus, gausah pake intro kayak 'Pada kesempatan kali ini, saya akan membahas tentang...', 'hey', dll, langsung aja ke topiknya."}
   {:role    "assistant"
    :content "Ok, formatnya gimana?"}
   {:role    "user"
    :content (str "Formatnya json, propertynya title, dan content. di title, isinya " section-title " aja.
     di content, elo elaborasikan section tadi jadi panjang dengan format html.")}])

(defn content-prompt-extender
  "Generate extended prompts for content in a section"
  [{:keys [article-title section-title section-description]}]
  [{:role    "system"
    :content "I'm a very good writer that can help you write enlightening articles that help people unlearn their wrong beliefs and learn new insights to see the world according to scientific knowledge and use good logical thinking"}
   {:role    "user"
    :content (str "Gue lagi mau nulis artikel tentang '" article-title "' yang dipecah jadi beberapa section")}
   {:role    "assistant"
    :content "Ok, gue bantu di bagian mana?"}
   {:role    "user"
    :content (str "Gue mau elo pecah penjelasan di salah satu section artikel tersebut, yaitu '" section-title "'. Ini outline dari chapter ini " section-description
                  "Kasih beberapa ide cara mecah section dari article ini jadi beberapa sub-topik untuk dikembangin lebih lanjut")}
   {:role    "assistant"
    :content "Ok, bahasanya gimana?"}
   {:role    "user"
    :content "Gue mau bahasanya in english yg informal, santai, witty, smart, and humorous tapi jangan cringe"}
   {:role    "assistant"
    :content "Ok, formatnya gimana?"}
   {:role    "user"
    :content (str "Formatnya json, dengan property 'title' & 'extended-description', isi title-nya itu '" section-title "' aja, lalu extended-description-nya itu hasil dari penjelasan yang udah elo kembangin tadi,
     tapi string aja ya formatnya jangan array.")}])

(defn gen-sections-prompt
  "Generate prompts for sections"
  [{:keys [title n-sections prompt]}]
  [{:role    "system"
    :content "I'm a very good writer that can help you write enlightening articles that help people unlearn their wrong beliefs and learn new insights to see the world according to scientific knowledge and use good logical thinking"}
   {:role    "user"
    :content (str "Ok, gue mau nulis article panjang yg dipecah jadi beberapa section tentang ini '" title "'")}
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
    :content "Gue mau bahasanya in English yg informal, santai, witty, smart, and humorous tapi jangan cringe"}
   {:role    "assistant"
    :content "Ok, formatnya gimana?"}
   {:role    "user"
    :content "Formatnya json, list of sections, tiap section ada property title & description, isi description basically cuma list of topics yang mesti dicover aja,
    ga usah pake penjelasan, semacam outline aja, tapi string aja ya formatnya jangan array, list yang banyak topiknya"}])


(defn gen-proset-prompt
  "The prompt composition for requesting proset"
  [{:keys [title content]}]
  [{:role "system"
    :content "Gue world class educator yang bisa bikin set of problems atau soals step-by-step yang bisa bikin murid paham konsep yang sulit dengan mudah"}
   {:role "user"
    :content (str "Gue lagi nulis artikel panjang dengan salah satu section-nya itu '" title "'.
    Gue butuh elo bikin 5 soal dengan jenis soal yang berbeda yang mengcover ini section tersebut. Jangan kurang atau lebih dari 5. Ini isinya: " content)}
   {:role "assistant"
    :content "Ok, kira-kira ada aturan khusus ngga?"}
   {:role "user"
    :content "Gue pengen soalnya didesain untuk menguji konsep dari isi section yang gue kasih. Jenis soalnya dikasih variasi dikit,
     soal pilihan ganda dengan lima pilihan ganda DAN satu jawaban bener. Pastiin pilihan yg salah ttp nyambung dari teks, tp salah gara2 fail to comprehend the text.
     Make sure jawaban yg bener itu seakurat mungkin."}
   {:role "assistant"
    :content "Ok, formatnya gimana?"}
   {:role "user"
    :content "Gue mau formatnya json, property-nya 'title' dan 'soals'.
    Property 'title' itu isinya judul section yg dikasih.
    Property 'soals' isinya list of soal. Tiap soal ada property 'text-soal' yang isinya pertanyaan yg elo generate dan 'choices' yg isinya list of object, which masing2 punya property ini:
    'option-text' yang ada teks pilihannya,
    'correct' untuk nandain jawaban tersebut bener (boolean, true untuk jawaban benar),
    dan 'idx' yang menunjukkan urutan jawaban (integer)"}])

(defn gen-proset-prompt-unused
  "The prompt composition for requesting proset"
  [{:keys [title content]}]
  [{:role "system"
    :content "Gue world class educator yang bisa bikin set of problems atau soals step-by-step yang bisa bikin murid paham konsep yang sulit dengan mudah"}
   {:role "user"
    :content (str "Gue lagi nulis artikel panjang dengan salah satu section-nya itu " title ".
    Nah gue butuh elo bikin 5 soal dengan topik yang berbeda yang modelnya mengcover ini section tersebut. Ini isinya: " content)}
   {:role "assistant"
    :content "Ok, kira-kira ada aturan khusus ngga?"}
   {:role "user"
    :content "Gue pengen soalnya didesain untuk menguji konsep dari isi section yang gue kasih. Jenis soalnya dikasih variasi dikit, soal pilihan ganda, boleh satu jawaban bener or banyak jawaban bener, nanti gue butuh explanation juga"}
   {:role "assistant"
    :content "Ok, formatnya gimana?"}
   {:role "user"
    :content "Gue mau formatnya json, hasilnya list of problems, tiap soal ada properti
              'text-soal', 'explanation', 'options'. Nah 'options' itu list of object yang masing2x propertinya ada 'text', 'correct?', 'idx'
              yang menunjukkan urutan jawaban (integer)"}])
