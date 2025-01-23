# Progetto Scalable and Cloud Programming - CoPurchase Analysis
### Istruzioni per eseguire il programma su DataProc

1. Prima di tutto creare un bucket su Google Cloud in cui caricare il dataset

2. Nel file src/main/scala/copurchase.scala del progetto inserire il percorso corretto del bucket in "val datasetPath" e "val outputPath". Inoltre, impostare "val numberOfNodes" col numero corretto di nodi del cluster che si vuole eseguire, ciò serve al calcolo della strong scaling efficiency.

3. Aprire la sbt shell e con il comando "package" generare il jar. Sarà possibile trovarlo nel path target/scala-2.12/progettoscalable_2.12-1.0.jar

4. Caricare il jar nello stesso bucket del dataset

5. Comandi usati per creare cluster:
   - gcloud dataproc clusters create <"nome cluster"> --region=us-central1 --single-node --master-boot-disk-size=500 --project=<"id progetto">
   - gcloud dataproc clusters create <"nome cluster"> --region=us-central1 --num-workers=2 --master-boot-disk-size=500 --worker-boot-disk-size=500 --project=<"id progetto">
   - gcloud dataproc clusters create <"nome cluster"> --region=us-central1 --num-workers=3 --master-boot-disk-size=500 --worker-boot-disk-size=500 --project=<"id progetto">
   - gcloud dataproc clusters create <"nome cluster"> --region=us-central1 --num-workers=4 --master-boot-disk-size=500 --worker-boot-disk-size=500 --project=<"id progetto">

6. Comando per eseguire il job: gcloud dataproc jobs submit spark --cluster=<"nome cluster"> --region=us-central1 --jar=gs://<"nome bucket">/<"file.jar"> --project=<"id progetto">

7. Eliminazione del cluster: gcloud dataproc clusters delete <"nome cluster"> --region us-central1 --project=<"id progetto">

8. Il file di output che viene generato nel bucket deve poi essere scaricato e ne va cambiata l'estensione in csv per visualizzarlo
