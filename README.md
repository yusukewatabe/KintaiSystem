この勤怠システムはバックエンドはJava、フレームワークはSpringBoot、  
フロントエンドはHTML,CSS,JavaScript、テンプレートエンジンはThymeleaf、CSSフレームワークはTailWindcssで作成しております。  
DBはMySQL、DBへのINSERT等はJPAを使用しています。

以下勤怠システムをクローン後のサーバー起動手順です。  
  
1.コマンドプロンプトもしくは、ターミナルにてクローンしたフォルダへ移動  
2.Mavenがインストールされている場合は「mvn clean install」を打鍵  
3.「mvn spring-boot:run」でサーバー起動  
4.サーバー起動後「http://localhost:8085/」へ接続  

ソースの修正を行ったら、画面をリロード(更新)することで最新ソースの状態の画面を表示できます。  

Mavenをインストールしていない場合は下記リンクを参照してダウンロードしてください。  
https://qiita.com/tarosa0001/items/e5667cfa857529900216  
