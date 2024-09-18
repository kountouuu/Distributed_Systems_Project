package Interactive_People;

import Broker.ListNotifier;
//Class that Activates the ListNotifier class so that all brokers stay updated .
public class ActivateNotifier {

	public static void main(String[] args) {
		ListNotifier activator = new ListNotifier();
		activator.NotifyServer(); //starts the NotifyServer method in ListNotifier .
	}

}
//C:\\Users\\dimit\\Desktop\\videos\\content_creator1