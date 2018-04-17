  CsvFileUtils csvFileUtils =
    new CsvFileUtils("quebec.txt");

  for (int i = 0; i < 2000; i++) {
      printArray(csvFileUtils
        .readOneLine());
  }
