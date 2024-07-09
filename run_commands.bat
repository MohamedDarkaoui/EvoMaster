java -Xmx8g -Xss4m -jar core/target/evomaster.jar ^
    --maxTime 1h^
    --sutControllerPort 40100 ^
    --outputFolder "C:\Users\moham\Desktop\thesis\benchmark\EMB\jdk_8_maven\em\embedded\rest\catwatch\src\unseeded_run3" ^
    --seedTestCases false ^
    --seedTestCasesFormat POSTMAN ^
    --seedTestCasesPath "postman-collections\catwatch.json" ^
    --writeStatistics true ^
    --snapshotInterval 50 ^
    --exportTestCasesDuringSeeding true ^
    --snapshotStatisticsFile "results\catwatch\unseeded\run3.csv"

pause