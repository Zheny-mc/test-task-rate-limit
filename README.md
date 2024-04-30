<h2>Необходимо реализовать на языке Java класс для работы с API Честного знака.</h2>
<p>Класс должен быть thread-safe и поддерживать ограничение на количество запросов к API. Ограничение указывается в конструкторе в виде количества запросов в определенный интервал времени.</p>
<p>Например: <strong>public CrptApi(TimeUnit timeUnit, int requestLimit)</strong></p>
<ul>
<li>timeUnit &ndash; указывает промежуток времени &ndash; секунда, минута и пр.</li>
<li>requestLimit &ndash; положительное значение, которое определяет максимальное количество запросов в этом промежутке времени.</li>
</ul>
<p>При превышении лимита запрос вызов должен блокироваться, чтобы не превысить максимальное количество запросов к API и продолжить выполнение, без выбрасывания исключения, когда ограничение на количество вызов API не будет превышено в результате этого вызова.</p>
<p>В любой ситуации превышать лимит на количество запросов запрещено для метода. Реализовать нужно единственный метод Создание документа для ввода в оборот товара, произведенного в РФ. Документ и подпись должны передаваться в метод в виде Java объекта и строки соответственно.</p>
<p>Вызывается по HTTPS метод POST следующий URL: <a href="https://ismp.crpt.ru/api/v3/lk/documents/create">ссылка</a></p>
<p>В теле запроса передается в формате JSON документ:</p>
<pre><code>{
  "description": {"participantInn": "string" },
  "doc_id": "string",
  "doc_status": "string",
  "doc_type": "LP_INTRODUCE_GOODS",
  "importRequest": true,
  "owner_inn": "string",
  "participant_inn": "string",
  "producer_inn": "string",
  "production_date": "2020-01-23",
  "production_type": "string",
  "products": [
    {
      "certificate_document": "string",
      "certificate_document_date": "2020-01-23",
      "certificate_document_number": "string",
      "owner_inn": "string",
      "producer_inn": "string",
      "production_date": "2020-01-23",
      "tnved_code": "string",
      "uit_code": "string",
      "uitu_code": "string"
    }
  ],
  "reg_date": "2020-01-23",
  "reg_number": "string"
}
</code>
</pre>
