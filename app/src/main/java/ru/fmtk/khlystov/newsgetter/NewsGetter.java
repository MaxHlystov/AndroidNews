package ru.fmtk.khlystov.newsgetter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.fmtk.khlystov.androidnews.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsGetter {

    @NonNull
    private final String countryCode;

    private final boolean online;

    private static final long idleTime = 2000; // 2 second

    @NonNull
    private static final String formatNewsURL = "https://newsapi.org/v2/top-headlines?country=%s&apiKey=%s";
    private static final int bufferSize = 8 * 1024;

    public NewsGetter(@Nullable String countryCode, boolean online) {
        this.countryCode = (countryCode != null) ? countryCode : "us";
        this.online = online;
    }

    public @Nullable
    Disposable observeNews(@NonNull Consumer<? super NewsResponse> onProc,
                           @NonNull Consumer<? super Throwable> onError) {
        Observable<NewsResponse> obs = Observable.create((ObservableEmitter<String> it) -> {
            // In accordance to the item 5 step 5 hw 4
            Thread.sleep(idleTime);
            if (online) {
                createOnlineRequest(it);
            } else {
                getOfflineNews(it);
            }
        })
                .map((String it) -> new Gson().fromJson(it, NewsResponse.class))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return obs.subscribe(onProc, onError);
    }

    private void createOnlineRequest(@NonNull ObservableEmitter<String> emitter) throws IOException {
        URL url = new URL(String.format(formatNewsURL, countryCode, BuildConfig.APIkey));
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        try {
            urlConn.connect();
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                emitter.onError(new RuntimeException(urlConn.getResponseMessage()));
            } else {
                InputStreamReader inputStreamReader = new InputStreamReader(urlConn.getInputStream(), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader, bufferSize);
                StringWriter buffer = new StringWriter();
                copyTo(bufferedReader, buffer);
                String s = buffer.toString();
                emitter.onNext(s);
            }
        } catch (IOException ex) {
            Log.d("NewsApp", "Error", ex);
        } finally {
            urlConn.disconnect();
        }
    }

    private void getOfflineNews(@NonNull ObservableEmitter<String> emitter) {
        emitter.onNext("{\"status\":\"ok\",\"totalResults\":20,\"articles\":[{\"source\":{\"id\":\"rbc\",\"name\":\"RBC\"},\"author\":null,\"title\":\"Росморпорт прокомментировал информацию о взрыве в здании в Петербурге\",\"description\":\"Информация о взрыве в здании Росморпорта в Санкт-Петербурге не соответствует действительности, заявили РБК в пресс-службе предприятия. Это происшествие не имеет к нам никакого отношения. Одно из ...\",\"url\":\"https://www.rbc.ru/rbcfreenews/5bb5cd509a794771d39cac97?from=newsfeed\",\"urlToImage\":\"https://s0.rbk.ru/v6_top_pics/media/img/6/88/755386429922886.jpg\",\"publishedAt\":\"2018-10-04T08:50:13Z\",\"content\":\"Информация о взрыве в здании Росморпорта в Санкт-Петербурге не соответствует действительности, заявили РБК в пресс-службе предприятия. «Это происшествие не имеет к нам никакого отношения. Одно из наших трех зданий расположено по адресу Межевой канал, 3. Инцид… [+1028 chars]\"},{\"source\":{\"id\":null,\"name\":\"Ura.news\"},\"author\":null,\"title\":\"В Ингушетии начались протесты со стрельбой против новой границы с Чечней. ФОТО, ВИДЕО\",\"description\":\"ФОТО, ВИДЕО\",\"url\":\"https://ura.news/news/1052353651\",\"urlToImage\":\"https://s.ura.news/images/news/upload/smm/2018/10/04/facebook_a6380f8d4cd2026349b78cd4ee725be3.jpg\",\"publishedAt\":\"2018-10-04T08:39:37Z\",\"content\":\"В городе Магас (республика Ингушетия) тысячи человек устроили несанкционированный митинг. Поводом для масштабной акции стало неприятие населением республики изменения границ с Чечней. Колонны из представителей местного населения движутся в сторону правительст… [+1521 chars]\"},{\"source\":{\"id\":null,\"name\":\"Interfax.ru\"},\"author\":null,\"title\":\"Консул Венгрии в Закарпатье объявлен персоной нон-грата\",\"description\":\"Послу Венгрии на Украине Эрно Кешкеню вручена нота о том, что консул этой страны в городе Берегово Закарпатской области объявлен персоной нон-грата и должен покинуть территорию Украины в течение 72 часов.\",\"url\":\"https://www.interfax.ru/world/631837\",\"urlToImage\":\"https://www.interfax.ru/aspimg/631837.png\",\"publishedAt\":\"2018-10-04T08:39:00Z\",\"content\":\"Москва. 4 октября. INTERFAX.RU - Послу Венгрии на Украине Эрно Кешкеню вручена нота о том, что консул этой страны в городе Берегово Закарпатской области объявлен персоной нон-грата и должен покинуть территорию Украины в течение 72 часов. Как сообщает в четвер… [+1454 chars]\"},{\"source\":{\"id\":\"rbc\",\"name\":\"RBC\"},\"author\":null,\"title\":\"Силуанов раскрыл детали плана по дедолларизации российской экономики\",\"description\":\"По словам первого вице-премьера, отказаться от обязательного возврата в Россию выручки экспортеров, производящих расчеты в рублях, планируется к 2024 году\",\"url\":\"https://www.rbc.ru/economics/04/10/2018/5bb5c95f9a7947709e4dac44\",\"urlToImage\":\"https://s0.rbk.ru/v6_top_pics/media/img/4/14/755386405815144.jpg\",\"publishedAt\":\"2018-10-04T08:27:25Z\",\"content\":\"По словам первого вице-премьера, отказаться от обязательного возврата в Россию выручки экспортеров, производящих расчеты в рублях, планируется к 2024 году Первый вице-премьер и глава Минфина Антон Силуанов раскрыл подробности плана по дедолларизации российско… [+2131 chars]\"},{\"source\":{\"id\":null,\"name\":\"Interfax.ru\"},\"author\":null,\"title\":\"Подавший в отставку мэр Владивостока не принял предложение продолжить работу\",\"description\":\"Мэр Владивостока Виталий Веркеенко, который накануне подал в отставку, не намерен менять своего решения после встречи с врио губернатора Приморья Олегом Кожемяко, который предложил ему \\\"отбросить эмоции и остаться работать\\\".\",\"url\":\"https://www.interfax.ru/russia/631833\",\"urlToImage\":\"https://www.interfax.ru/aspimg/631833.png\",\"publishedAt\":\"2018-10-04T08:26:14Z\",\"content\":\"Москва. 4 октября. INTERFAX.RU - Мэр Владивостока Виталий Веркеенко, который накануне подал в отставку, не намерен менять своего решения после встречи с врио губернатора Приморья Олегом Кожемяко, который предложил ему \\\"отбросить эмоции и остаться работать\\\". \\\"… [+2299 chars]\"},{\"source\":{\"id\":null,\"name\":\"Rg.ru\"},\"author\":null,\"title\":\"Видео: Украинцам показали закрытую фабрику Порошенко в Липецке\",\"description\":\"Украинский журналист Роман Цимбалюк посетил кондитерскую фабрику Roshen Петра Порошенко в Липецке и сообщил, что она не работает.\",\"url\":\"https://rg.ru/2018/10/04/video-ukraincam-pokazali-zakrytuiu-fabriku-poroshenko-v-lipecke.html\",\"urlToImage\":\"//cdnimg.rg.ru/img/content/159/92/06/333333333_t_650x433.jpg\",\"publishedAt\":\"2018-10-04T08:06:00Z\",\"content\":\"Украинский журналист Роман Цимбалюк посетил кондитерскую фабрику Roshen Петра Порошенко в Липецке и сообщил, что она не работает. \\\"Важное объявление: в этот город надо приезжать со своими конфетами Roshen. Здесь давно ничего не производят. Заводы стоят, местн… [+675 chars]\"},{\"source\":{\"id\":\"rbc\",\"name\":\"RBC\"},\"author\":null,\"title\":\"Замгенпрокурора России погиб при падении вертолета в Костромской области\",\"description\":\"Саак Карапетян занимал должность заместителя генпрокурора с 2016 года. Вертолет упал вечером в среду, 3 октября, на северо-западе Костромской области. Помимо Карапетяна, на его борту находились еще двое, они тоже погибли\",\"url\":\"https://www.rbc.ru/politics/04/10/2018/5bb5c0a29a79476e95fff1cc?from=main\",\"urlToImage\":\"https://s0.rbk.ru/v6_top_pics/media/img/9/91/755386382634919.jpg\",\"publishedAt\":\"2018-10-04T07:31:15Z\",\"content\":\"Саак Карапетян занимал должность заместителя генпрокурора с 2016 года. Вертолет упал вечером в среду, 3 октября, на северо-западе Костромской области. Помимо Карапетяна, на его борту находились еще двое, они тоже погибли Среди погибших в результате крушения в… [+2161 chars]\"},{\"source\":{\"id\":null,\"name\":\"Snob.ru\"},\"author\":null,\"title\":\"Россия заняла второе место после Индии по числу смертей во время селфи\",\"description\":null,\"url\":\"https://snob.ru/news/166540\",\"urlToImage\":null,\"publishedAt\":\"2018-10-04T07:20:00Z\",\"content\":null},{\"source\":{\"id\":null,\"name\":\"Gazeta.ru\"},\"author\":null,\"title\":\"Скрытые агенты в США: Китая боятся больше, чем России\",\"description\":null,\"url\":\"https://www.gazeta.ru/politics/2018/10/04_a_12007903.shtml\",\"urlToImage\":null,\"publishedAt\":\"2018-10-04T06:58:06Z\",\"content\":null},{\"source\":{\"id\":null,\"name\":\"Business-gazeta.ru\"},\"author\":null,\"title\":\"Золотов лично предотвратил взрыв на Красной площади, вступив в переговоры с «террористом»\",\"description\":null,\"url\":\"https://www.business-gazeta.ru/news/397650\",\"urlToImage\":null,\"publishedAt\":\"2018-10-04T06:33:55Z\",\"content\":null},{\"source\":{\"id\":null,\"name\":\"Interfax.ru\"},\"author\":null,\"title\":\"Великобритания впервые прямо обвинила ГРУ в причастности к кибератакам\",\"description\":\"Глава МИД страны Джереми Хант пообещал ответные меры\",\"url\":\"https://www.interfax.ru/world/631810\",\"urlToImage\":\"https://www.interfax.ru/aspimg/631810.png\",\"publishedAt\":\"2018-10-04T06:32:00Z\",\"content\":\"Москва. 4 октября. INTERFAX.RU - Глава МИД Великобритании Джереми Хант заявил, что ГРУ стоит за несколькими кибератаками в мире. По данным телеканала Sky News, это первый случай, когда британские власти прямо обвиняют ГРУ во враждебных действиях в киберпростр… [+1285 chars]\"},{\"source\":{\"id\":null,\"name\":\"Tvrain.ru\"},\"author\":\"TVRAIN\",\"title\":\"«Левада-центр»: рейтинг доверия Путину вернулся к «докрымскому» уровню\",\"description\":\"Рейтинг доверия к президенту России Владимиру Путину снизился до 58% — это «докрымский» уровень 2013 года. Об этом сообщает «Коммерсантъ» со ссылкой на результаты опроса «Левада-центра».\",\"url\":\"https://tvrain.ru/news/levada_tsentr_rejting_doverija_putinu_vernulsja_k_dokrymskomu_urovnju-472662/\",\"urlToImage\":\"https://s79369.cdn.ngenix.net/media/articles_share_images/47/26/62/image.png?2018_10_04_09_23_27\",\"publishedAt\":\"2018-10-04T06:23:22Z\",\"content\":\"Рейтинг доверия к президенту России Владимиру Путину снизился до 58% — это «докрымский» уровень 2013 года. Об этом сообщает «Коммерсантъ» со ссылкой на результаты опроса «Левада-центра». Путину доверяют меньше россиян, чем российской армии. Согласно опросу, 6… [+827 chars]\"},{\"source\":{\"id\":null,\"name\":\"Kommersant.ru\"},\"author\":null,\"title\":\"РЖД может заменить полки в плацкарте на капсулы\",\"description\":\"Подробнее на сайте\",\"url\":\"https://www.kommersant.ru/doc/3759741\",\"urlToImage\":\"https://im2.kommersant.ru/SocialPics/3759741_26_0_194059115\",\"publishedAt\":\"2018-10-04T06:12:02Z\",\"content\":\"РЖД рассматривает идею оснащения плацкартных вагонов капсулами и пространствами-трансформерами, сообщил гендиректор компании Олег Белозеров. «На базе принципов формирования общего и плацкартного вагонов будут созданы зоны персонального комфорта. У пассажира д… [+818 chars]\"},{\"source\":{\"id\":null,\"name\":\"Vz.ru\"},\"author\":null,\"title\":\"В ВМС Украины рассказали о готовности к бою во время прохода Керченского пролива\",\"description\":\"Замначальника штаба морского командования ВМС Украины Дмитрий Коваленко, командовавший походом двух украинских кораблей мимо берегов Крыма и через Керченский пролив, рассказал о готовности к боестолкновению.\",\"url\":\"https://vz.ru/news/2018/10/4/944499.html\",\"urlToImage\":\"https://img.vz.ru/upimg/soc/soc_944499.jpg\",\"publishedAt\":\"2018-10-04T06:09:04Z\",\"content\":\"Замначальника штаба морского командования ВМС Украины Дмитрий Коваленко, командовавший походом двух украинских кораблей мимо берегов Крыма и через Керченский пролив, рассказал о готовности к боестолкновению. Коваленко рассказал, что в начале прохода через Кер… [+1453 chars]\"},{\"source\":{\"id\":null,\"name\":\"Banki.ru\"},\"author\":null,\"title\":\"Bloomberg рассказал о конце «тирании» доллара\",\"description\":\"Гегемония доллара начала разрушаться, а мировые лидеры, которые ранее признавали сложившееся положение дел, теперь стремятся отказаться от американской валюты, пишет агентство Bloomberg.\",\"url\":\"http://www.banki.ru/news/lenta/?id=10686505\",\"urlToImage\":\"https://static1.banki.ru/ugc/ce/b7/ad/c4/preview_image2.jpg\",\"publishedAt\":\"2018-10-04T05:36:00Z\",\"content\":\"Гегемония доллара начала разрушаться, а мировые лидеры, которые ранее признавали сложившееся положение дел, теперь стремятся отказаться от американской валюты, пишет агентство Bloomberg. Так, председатель Еврокомиссии Жан-Клод Юнкер в сентябре назвал «абсурдн… [+1447 chars]\"},{\"source\":{\"id\":\"rbc\",\"name\":\"RBC\"},\"author\":null,\"title\":\"Пентагон анонсировал ускоренное начало испытаний гиперзвукового оружия\",\"description\":\"США раньше, чем планировалось, начнут испытания собственного гиперзвукового оружия, утверждают в Пентагоне. Ранее в Минобороны США заявляли о разработке прототипа подобного оружия к 2023 году\",\"url\":\"https://www.rbc.ru/politics/04/10/2018/5bb597359a794765c2d9ca4a\",\"urlToImage\":\"https://s0.rbk.ru/v6_top_pics/media/img/7/89/755386297642897.jpg\",\"publishedAt\":\"2018-10-04T05:09:43Z\",\"content\":\"США раньше, чем планировалось, начнут испытания собственного гиперзвукового оружия, утверждают в Пентагоне. Ранее в Минобороны США заявляли о разработке прототипа подобного оружия к 2023 году Замглавы Пентагона Патрик Шанахан в беседе с журналистами заявил, ч… [+1912 chars]\"},{\"source\":{\"id\":\"rbc\",\"name\":\"RBC\"},\"author\":null,\"title\":\"Вице-спикер Рады призвала Россию вернуть 30 тыс. «украденных» вагонов\",\"description\":\"Ирина Геращенко обвинила Россию в торговле «ворованными» вагонами. Также она заявила о краже 85 «новеньких» вагонов, закупленных на выданный ЕБРР кредит, который Киев пока не вернул\",\"url\":\"https://www.rbc.ru/rbcfreenews/5bb5549f9a79475a600a20f9\",\"urlToImage\":\"https://s0.rbk.ru/v6_top_pics/media/img/8/27/755386105449278.jpg\",\"publishedAt\":\"2018-10-04T01:04:33Z\",\"content\":\"Ирина Геращенко обвинила Россию в торговле «ворованными» вагонами. Также она заявила о краже 85 «новеньких» вагонов, закупленных на выданный ЕБРР кредит, который Киев пока не вернул Власти Украины требуют от России вернуть ПАО «Украинская железная дорога» («У… [+1465 chars]\"},{\"source\":{\"id\":null,\"name\":\"19rus.info\"},\"author\":\"2\",\"title\":\"Губернатор Петербурга Полтавченко ушел в отставку\",\"description\":\"В губернаторском корпусе продолжаются перетасовки. Глава государства Владимир Путин предложил полномочному представителю президента РФ в Северо-западн...\",\"url\":\"http://www.19rus.info/index.php/vlast-i-politika/item/91221-gubernator-peterburga-poltavchenko-ushel-v-otstavku\",\"urlToImage\":\"http://www.19rus.info/media/k2/items/cache/142b790ee590b26033a3aee1293a5d3c_M.jpg\",\"publishedAt\":\"2018-10-04T00:18:00Z\",\"content\":\"В губернаторском корпусе продолжаются перетасовки. Глава государства Владимир Путин предложил полномочному представителю президента РФ в Северо-западном федеральном округе Александру Беглову стать временно исполняющим обязанности губернатора Санкт-Петербурга,… [+9627 chars]\"},{\"source\":{\"id\":null,\"name\":\"Bashinform.ru\"},\"author\":\"\",\"title\":\"В России подписан закон о повышении возраста выхода на пенсию\",\"description\":\"Президент России Владимир Путин подписал федеральный закон «О внесении изменений в отдельные законодательные акты Российской Федерации по вопросам назнач...\",\"url\":\"http://www.bashinform.ru/news/1220039-v-rossii-podpisan-zakon-o-povyshenii-vozrasta-vykhoda-na-pensiyu/\",\"urlToImage\":\"http://www.bashinform.ru/upload/img_res1280/545cf2672a3c5479/yrov4971_1_jpg_ejw_1280_jpg_crop1538594019_ejw_1280.jpg\",\"publishedAt\":\"2018-10-03T19:13:49Z\",\"content\":\"В России подписан закон о повышении возраста выхода на пенсию Фото: Олег Яровиков УФА, 3 окт 2018. /ИА «Башинформ»/. Президент России Владимир Путин подписал федеральный закон «О внесении изменений в отдельные законодательные акты Российской Федерации по вопр… [+2854 chars]\"},{\"source\":{\"id\":null,\"name\":\"Tass.ru\"},\"author\":\"ТАСС\",\"title\":\"Порошенко внес в Раду законопроект о продлении особого статуса Донбасса\",\"description\":\"Действие закона заканчивается 6 октября 2018 года\",\"url\":\"https://tass.ru/mezhdunarodnaya-panorama/5630208\",\"urlToImage\":\"https://phototass1.cdnvideo.ru/width/1200_4ce85301/tass/m2/uploads/i/20181003/4821873.jpg\",\"publishedAt\":\"2018-10-03T07:20:34Z\",\"content\":\"КИЕВ, 3 октября. /ТАСС/. Президент Украины Петр Порошенко просит Верховную раду безотлагательно рассмотреть проект закона о продлении особого статуса неподконтрольных Киеву территорий Донбасса. Соответствующий документ зарегистрирован в парламенте от имени гл… [+2258 chars]\"}]}");
    }

    private long copyTo(@NonNull BufferedReader bufferedReader,
                        @NonNull Writer out) throws IOException {
        long charsCopied = 0;
        char[] buffer = new char[bufferSize];
        while (true) {
            int chars = bufferedReader.read(buffer);
            if (chars < 0) {
                break;
            }
            out.write(buffer, 0, chars);
            charsCopied += chars;
        }
        return charsCopied;
    }
}
