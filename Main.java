// package gitlet;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */
    public static void main(String... args) {

        Repo r = new Repo();
        int inputLength = args.length;
        if (inputLength == 0) {
            System.out.println("Please enter a command.");
        } else {
            switch (args[0]) {
                case "init": {
                    if (inputChecker(1, args)) {
                        r.init();
                    }
                    break;
                }
                case "add": {
                    if (inputChecker(2, args)) {
                        r.add(args[1]);
                    }
                    break;
                }
                case "commit": {
                    if (inputChecker(2, args)) {
                        r.commitment(args[1]);
                    }
                    break;
                }
                case "rm": {
                    if (inputChecker(2, args)) {
                        r.rm(args[1]);
                    }
                    break;
                }
                case "log":
                    if (inputChecker(1, args)) {
                        r.log();
                    }
                    break;
                case "global-log":
                    if (inputChecker(1, args)) {
                        r.global();
                    }
                    break;
                case "branch": {
                    if (inputChecker(2, args)) {
                        String branchName = args[1];
                        r.branch(branchName);
                    }
                    break;
                }
                case "rm-branch": {
                    if (inputChecker(2, args)) {
                        String branchName = args[1];
                        r.rmb(branchName);
                    }
                    break;
                }

                default:
                    System.out.println("No command with that name exists.");
            }
        }
        System.exit(0);
    }

    /*
    * Checks that the number of inputs sent to Main matches the
    * number expected
     */

    static boolean inputChecker(int length, String... args) {
        if (args.length == length) {
            return true;
        }
        System.out.println("Incorrect Operands");
        return false;
    }
}