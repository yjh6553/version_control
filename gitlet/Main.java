package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Joshua Yoo
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        Gitrepo repo = new Gitrepo();
        if (args.length == 0) {
            System.out.println("Please enter a command.");
        } else {
            switch (args[0]) {
            case "init":
                repo.init();
                break;
            case "commit":
                repo.commit(args[1], false, null);
                break;
            case "add":
                repo.add(args[1]);
                break;
            case "log":
                repo.log();
                break;
            case "checkout":
                if (args.length == 3) {
                    repo.checkout1(args);
                    break;
                } else if (args.length == 4) {
                    repo.checkout2(args);
                    break;
                } else if (args.length == 2) {
                    repo.checkout3(args);
                    break;
                }
            case "rm" :
                repo.rm(args[1]);
                break;
            case "find" :
                repo.find(args[1]);
                break;
            case "status" :
                repo.status();
                break;
            case "global-log" :
                repo.globalLog();
                break;
            case "reset" :
                repo.reset(args[1]);
                break;
            case "branch" :
                repo.branch(args[1]);
                break;
            case "rm-branch" :
                repo.rmBranch(args[1]);
                break;
            case "merge" :
                repo.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
            }
        }

    }
}
