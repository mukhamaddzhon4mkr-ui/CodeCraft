package com.example.codecraft.data.model

object PythonContent {
    val lessons = listOf(
        Lesson(
            id = "py_01",
            language = "Python",
            title = "1. Знакомство с print()",
            description = "Твой первый код в Python.",
            theoryText = "Функция print() выводит текст на экран. Текст должен быть в кавычках.\nПример: print(\"Привет, Мир!\")",
            question = "Какой код выведет на экран фразу: Hello?",
            options = listOf("print(Hello)", "print(\"Hello\")", "echo \"Hello\"", "say(\"Hello\")"),
            correctAnswer = "print(\"Hello\")",
            rewardPoints = 100
        ),
        Lesson(
            id = "py_02",
            language = "Python",
            title = "2. Переменные",
            description = "Где хранить данные.",
            theoryText = "Переменная — это имя для значения. В Python не нужно указывать тип, он определится сам.\nПример: age = 25",
            question = "Как создать переменную x со значением 10?",
            options = listOf("int x = 10", "var x = 10", "x = 10", "x := 10"),
            correctAnswer = "x = 10",
            rewardPoints = 100
        ),
        Lesson(
            id = "py_03",
            language = "Python",
            title = "3. Типы данных",
            description = "Числа и строки.",
            theoryText = "Основные типы: int (целые числа), float (дробные), str (строки в кавычках).",
            question = "К какому типу относится значение 5.5?",
            options = listOf("int", "float", "str", "bool"),
            correctAnswer = "float",
            rewardPoints = 100
        ),
        Lesson(
            id = "py_04",
            language = "Python",
            title = "4. Списки (Lists)",
            description = "Работа с массивами элементов.",
            theoryText = "Списки позволяют хранить много элементов. Они пишутся в квадратных скобках.\nПример: items = [1, 2, 3]",
            question = "Как получить первый элемент списка a?",
            options = listOf("a[1]", "a[0]", "a{0}", "a.first()"),
            correctAnswer = "a[0]",
            rewardPoints = 100
        ),
        Lesson(
            id = "py_05",
            language = "Python",
            title = "5. Условия (If)",
            description = "Логика программы.",
            theoryText = "Используй if для проверки условий. Не забывай про двоеточие и отступы!\nПример:\nif x > 0:\n    print('Positive')",
            question = "Как проверить, что a равно b?",
            options = listOf("if a = b:", "if a == b:", "if a is b", "if a equals b"),
            correctAnswer = "if a == b:",
            rewardPoints = 100
        ),
        Lesson(
            id = "py_06",
            language = "Python",
            title = "6. Цикл For",
            description = "Повторение для каждого элемента.",
            theoryText = "Цикл for перебирает элементы последовательности.\nПример:\nfor i in range(5):\n    print(i)",
            question = "Какая функция создает диапазон чисел?",
            options = listOf("list()", "range()", "generate()", "sequence()"),
            correctAnswer = "range()",
            rewardPoints = 100
        ),
        Lesson(
            id = "py_07",
            language = "Python",
            title = "7. Словари (Dict)",
            description = "Ключи и значения.",
            theoryText = "Словари хранят пары 'ключ: значение' в фигурных скобках.\nПример: user = {'id': 1, 'name': 'Alex'}",
            question = "Какими скобками задается словарь?",
            options = listOf("[]", "()", "{}", "<>"),
            correctAnswer = "{}",
            rewardPoints = 100
        ),
        Lesson(
            id = "py_08",
            language = "Python",
            title = "8. Функции (Def)",
            description = "Создание своих команд.",
            theoryText = "Функции начинаются со слова def. Это позволяет использовать код многократно.\nПример: def my_func():",
            question = "С какого слова начинается объявление функции?",
            options = listOf("function", "fun", "def", "task"),
            correctAnswer = "def",
            rewardPoints = 100
        ),
        Lesson(
            id = "py_09",
            language = "Python",
            title = "9. Ввод данных (input)",
            description = "Общение с пользователем.",
            theoryText = "Функция input() позволяет пользователю ввести текст с клавиатуры.",
            question = "Какая функция используется для ввода данных?",
            options = listOf("read()", "get()", "input()", "scan()"),
            correctAnswer = "input()",
            rewardPoints = 100
        ),
        Lesson(
            id = "py_10",
            language = "Python",
            title = "10. Комментарии",
            description = "Заметки в коде.",
            theoryText = "Комментарии начинаются с символа # и игнорируются программой.",
            question = "Какой символ начинает однострочный комментарий?",
            options = listOf("//", "/*", "#", "--"),
            correctAnswer = "#",
            rewardPoints = 100
        )
    )
}
