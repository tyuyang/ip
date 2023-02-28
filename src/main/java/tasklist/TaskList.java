package tasklist;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.ToDo;

public class TaskList {
    protected ArrayList<Task> tasks;
    protected int noOfTasks;
    private TasklistUi ui;

    private boolean isValidTaskNo(int taskNo) {
        if (taskNo <= 0) {
            ui.printNegTaskNoError();
            return false;
        }
        if (taskNo > noOfTasks) {
            ui.printOOBTaskNoError();
            return false;
        }
        return true;
    }

    private void parseTasks(String[] args) {
        switch (args[0]) {
        case "T":
            tasks.add(new ToDo(args[2]));
            tasks.get(tasks.size() - 1).setStatus(args[1].equals("Y"));
            break;
        case "D":
            tasks.add(new Deadline(args[2], args[3]));
            tasks.get(tasks.size() - 1).setStatus(args[1].equals("Y"));
            break;
        case "E":
            tasks.add(new Event(args[2], args[3], args[4]));
            tasks.get(tasks.size() - 1).setStatus(args[1].equals("Y"));
            break;
        }
    }

    public TaskList() {
        this.tasks = new ArrayList<>();
        noOfTasks = 0;
        this.ui = new TasklistUi(this);
    }

    public TaskList(Scanner s) {
        this.tasks = new ArrayList<>();
        this.noOfTasks = 0;
        while (s.hasNextLine()) {
            noOfTasks++;
            String line = s.nextLine();
            String[] args = line.split("\\|");
            parseTasks(args);
        }
        this.ui = new TasklistUi(this);
        listTasks();
    }

    private boolean hasBlankArgument(String arg, String type) {
        if(arg.isEmpty()) {
            noOfTasks--;
            ui.printBlankArgumentError(type);
            return true;
        }
        return false;
    }

    private boolean hasMissingKeyword(int index, String type) {
        if (index == -1) {
            noOfTasks--;
            ui.printMissingKeywordError(type);
            return true;
        }
        return false;
    }

    private boolean hasDelimiter(String args) {
        if(args.contains("|")) {
            noOfTasks--;
            ui.printDelimiterFoundError();
            return true;
        }
        return false;
    }

    public void addToDo(String args) {
        noOfTasks++;
        args = args.trim();
        if(hasBlankArgument(args, "Name")) {
            return;
        }
        if(hasDelimiter(args)) {
            return;
        }
        tasks.add(new ToDo(args));
        ui.printAddTaskMessage(noOfTasks);
    }

    public void addDeadline(String args) {
        noOfTasks++;
        int indexOfBy = args.indexOf("/by");
        if (hasMissingKeyword(indexOfBy, "/by")) {
            return;
        }
        String name = args.substring(0, indexOfBy);
        name = name.trim();
        if (hasBlankArgument(name, "Name")) {
            return;
        }
        indexOfBy += 3;
        String by = args.substring(indexOfBy);
        by = by.trim();
        if (hasBlankArgument(by, "by")) {
            return;
        }
        if (hasDelimiter(name) || hasDelimiter(by)) {
            return;
        }
        tasks.add(new Deadline(name, by));
        ui.printAddTaskMessage(noOfTasks);
    }

    public void addEvent(String args) {
        noOfTasks++;
        int indexOfFrom = args.indexOf("/from");
        int indexOfTo = args.indexOf("/to");
        if (hasMissingKeyword(indexOfFrom, "/from")) {
            return;
        }
        if (hasMissingKeyword(indexOfTo, "/to")) {
            return;
        }
        if (indexOfFrom > indexOfTo) {
            noOfTasks--;
            ui.printFromBeforeToError();
            return;
        }
        String name = args.substring(0, indexOfFrom);
        name = name.trim();
        if (hasBlankArgument(name, "Name")) {
            return;
        }
        indexOfFrom += 5;
        String from = args.substring(indexOfFrom, indexOfTo);
        from = from.trim();
        if (hasBlankArgument(from, "from")) {
            return;
        }
        indexOfTo += 3;
        String to = args.substring(indexOfTo);
        to = to.trim();
        if (hasBlankArgument(to, "to")) {
            return;
        }
        if (hasDelimiter(name) || hasDelimiter(from) || hasDelimiter(to)) {
            return;
        }
        tasks.add(new Event(name, from, to));
        ui.printAddTaskMessage(noOfTasks);
    }

    public void listTasks() {
        ui.printAllTasks();
    }

    public void markDone(int taskNo) {
        if (!isValidTaskNo(taskNo)) {
            return;
        }
        if (tasks.get(taskNo - 1).isDone()) {
            ui.printAlreadyDoneMessage();
        } else {
            tasks.get(taskNo - 1).setStatus(true);
            ui.printMarkDoneMessage(taskNo);
        }
    }

    public void unmarkDone(int taskNo) {
        if (!isValidTaskNo(taskNo)) {
            return;
        }
        if (!tasks.get(taskNo - 1).isDone()) {
            ui.printNotDoneMessage();
        } else {
            tasks.get(taskNo - 1).setStatus(false);
            ui.printUnmarkDoneMessage(taskNo);
        }
    }

    public void deleteTask(int taskNo) {
        if (!isValidTaskNo(taskNo)) {
            return;
        }
        noOfTasks--;
        ui.printDeleteTaskMessage(taskNo);
        tasks.remove(taskNo - 1);
    }

    public void saveToFile(FileWriter fileWriter) throws IOException {
        for (Task task : tasks) {
            fileWriter.write(task.toSaveString() + System.lineSeparator());
        }
    }

    public void findTask(String keyword) {
        if (keyword.isEmpty()) {
            ui.printBlankArgumentError("Keyword");
            return;
        }
        keyword.trim();
        int taskNo = 1;
        boolean taskFound = false;
        for (Task task : tasks) {
            if (task.getName().contains(keyword)) {
                if (!taskFound) {
                    taskFound = true;
                    ui.printTaskFoundMessage(keyword);
                }
                ui.printTaskWithNumber(taskNo);
            }
            taskNo++;
        }
        if (!taskFound) {
            System.out.println("Sorry, no tasks found with this keyword.");
        }
    }
}
