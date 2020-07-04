package ly.unnecessary.frontend;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import ly.unnecessary.backend.api.CommunityOuterClass.Chat;
import ly.unnecessary.backend.api.CommunityOuterClass.Community;

public class CommunityComponent {
    private Function<String, Integer> onCreateChat;
    private TextField newChatField;
    private VBox chatList;
    private VBox channel;
    private ScrollPane chatListWrapper;
    private VBox communities;

    public void addChat(Chat chat) {
        this.chatList.getChildren().add(this.createChat(channel.widthProperty(), "FP", chat.getMessage(), true));
    }

    public void replaceChats(List<Chat> chats) {
        this.chatList.getChildren()
                .setAll(chats.stream().map(c -> this.createChat(channel.widthProperty(), "FP", c.getMessage(), true))
                        .collect(Collectors.toList()));
    }

    public void setOnCreateChat(Function<String, Integer> onCreateChat) {
        this.onCreateChat = onCreateChat;
    }

    public void clearAndFocusNewChatFieldText() {
        this.newChatField.clear();
        this.newChatField.requestFocus();
    }

    public void scrollChatsToBottom() {
        var animation = new Timeline(
                new KeyFrame(Duration.seconds(0.5), new KeyValue(this.chatListWrapper.vvalueProperty(), 1)));

        animation.play();
    }

    public void replaceCommunities(List<Community> communities) {
        this.communities.getChildren().setAll(communities.stream()
                .map(c -> this.createCommunityLink("FP", false, c.getDisplayName())).collect(Collectors.toList()));
    }

    public Node render() {
        var wrapper = new BorderPane();

        // Community switcher
        var communitySwitcher = new VBox();

        var communityList = new ScrollPane();

        this.communities = new VBox();
        communities.setSpacing(8);
        communities.setPadding(new Insets(8, 0, 8, 8));

        communityList.setContent(communities);
        communityList.setVbarPolicy(ScrollBarPolicy.NEVER);
        communityList.setHbarPolicy(ScrollBarPolicy.NEVER);
        communityList.setStyle("-fx-background-color: transparent");

        VBox.setVgrow(communityList, Priority.ALWAYS);

        var communityAddButton = this.createCommunityAction(FontAwesomeSolid.PLUS, "Create community");
        var communityJoinButton = this.createCommunityAction(FontAwesomeSolid.DOOR_OPEN, "Join community");

        var communityMainActions = new VBox(communityAddButton, communityJoinButton);
        communityMainActions.setSpacing(8);
        communityMainActions.setPadding(new Insets(8));

        communitySwitcher.getChildren().addAll(communityList, communityMainActions);

        // Community details
        var communityDetails = new VBox();

        var avatarHeader = this.createUserMenu("FP", "Felicitas Pojtinger");
        avatarHeader.setMaxWidth(Double.MAX_VALUE);

        var memberListWrapper = new ScrollPane();

        var userList = new VBox();

        var ownerList = new VBox();

        ownerList.getChildren().addAll(this.createHeader("Owner"), this.createUserPersona("FP", "Felicitas Pojtinger"));
        ownerList.setSpacing(8);
        ownerList.setPadding(new Insets(0, 0, 8, 0));

        var memberList = new VBox();

        var invitePeopleButton = this.createPrimaryAction(FontAwesomeSolid.USER_PLUS, "Invite people");
        invitePeopleButton.setMaxWidth(Double.MAX_VALUE);

        memberList.getChildren().addAll(this.createHeader("Members"), this.createUserPersona("AD", "Alice Duck"),
                this.createUserPersona("BO", "Bob Oliver"), this.createUserPersona("PK", "Peter Kropotkin"),
                invitePeopleButton);
        memberList.setSpacing(8);
        memberList.setPadding(new Insets(0, 0, 8, 0));

        userList.getChildren().addAll(ownerList, memberList);

        memberListWrapper.setVbarPolicy(ScrollBarPolicy.NEVER);
        memberListWrapper.setHbarPolicy(ScrollBarPolicy.NEVER);
        memberListWrapper.setStyle("-fx-background-color: transparent");
        memberListWrapper.setContent(userList);

        VBox.setVgrow(memberListWrapper, Priority.ALWAYS);

        communityDetails.getChildren().addAll(memberListWrapper, avatarHeader);
        communityDetails.setSpacing(8);
        communityDetails.setPadding(new Insets(8));

        // Community content
        var communityContent = new HBox();

        var communityHeader = new HBox();
        communityHeader.getChildren().add(this.createHeader("Community 1"));

        var communityChannels = new VBox();

        var communityChannelsList = new ListView<>();
        communityChannelsList.getItems().addAll(new Label("Channel 1"), new Label("Channel 2"), new Label("Channel 3"));
        communityChannelsList.setMaxWidth(175);

        var addChannelButtonWrapper = new HBox();
        var addChannelButton = createPrimaryAction(FontAwesomeSolid.PLUS_SQUARE, "Create channel");
        addChannelButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(addChannelButton, Priority.ALWAYS);
        addChannelButtonWrapper.getChildren().add(addChannelButton);
        addChannelButtonWrapper.setAlignment(Pos.CENTER);
        addChannelButtonWrapper.setMinHeight(56);

        VBox.setVgrow(communityChannelsList, Priority.ALWAYS);

        communityChannels.getChildren().addAll(communityHeader, communityChannelsList, addChannelButtonWrapper);
        communityChannels.setSpacing(8);

        this.channel = new VBox();

        var channelHeader = new HBox();
        channelHeader.getChildren().add(this.createHeader("Channel 1"));

        this.chatListWrapper = new ScrollPane();

        chatListWrapper.setVbarPolicy(ScrollBarPolicy.NEVER);
        chatListWrapper.setHbarPolicy(ScrollBarPolicy.NEVER);
        chatListWrapper.setFitToWidth(true);

        this.chatList = new VBox();
        chatList.setSpacing(8);
        chatListWrapper.setContent(chatList);
        chatListWrapper.setHbarPolicy(ScrollBarPolicy.NEVER);
        chatListWrapper.setStyle("-fx-background-color: transparent");

        var newChatWrapper = new HBox();
        this.newChatField = new TextField();
        newChatField.setPromptText("New chat");
        newChatField.setPadding(new Insets(9));
        newChatField.setStyle("-fx-background-radius: 16 0 0 16");
        newChatField.setOnAction((e) -> this.onCreateChat.apply(newChatField.getText()));
        var sendChatButton = new Button();
        var sendIcon = new FontIcon(FontAwesomeSolid.PAPER_PLANE);
        sendIcon.setIconColor(Paint.valueOf("white"));
        sendChatButton.setGraphic(sendIcon);
        sendChatButton.setStyle("-fx-background-radius: 0 16 16 0; -fx-base: royalblue");
        sendChatButton.setPadding(new Insets(9, 14, 9, 9));
        sendChatButton.setOnAction((e) -> this.onCreateChat.apply(newChatField.getText()));
        HBox.setHgrow(newChatField, Priority.ALWAYS);
        newChatWrapper.getChildren().addAll(newChatField, sendChatButton);
        newChatWrapper.setAlignment(Pos.CENTER);
        newChatWrapper.setMinHeight(56);

        VBox.setVgrow(chatListWrapper, Priority.ALWAYS);

        channel.getChildren().addAll(channelHeader, chatListWrapper, newChatWrapper);
        channel.setSpacing(8);

        HBox.setHgrow(channel, Priority.ALWAYS);

        communityContent.getChildren().addAll(communityChannels, channel);
        communityContent.setSpacing(8);
        communityContent.setPadding(new Insets(8));

        wrapper.setLeft(communitySwitcher);
        wrapper.setCenter(communityContent);
        wrapper.setRight(communityDetails);

        Platform.runLater(() -> newChatField.requestFocus());

        wrapper.setStyle("-fx-font-family: 'Arial';");

        return wrapper;
    }

    private Button createCommunityLink(String initials, boolean active, String fullName) {
        var link = new Button(initials);
        var tooltip = new Tooltip(fullName);
        Tooltip.install(link, tooltip);

        if (active) {
            link.setStyle("-fx-base: royalblue; -fx-background-radius: 16; " + SIDEBAR_BUTTON_STYLES);
        } else {
            link.setStyle("-fx-background-radius: 32; " + SIDEBAR_BUTTON_STYLES);
        }

        return link;
    }

    private Button createCommunityAction(Ikon iconName, String action) {
        var button = new Button();
        button.setGraphic(new FontIcon(iconName));
        var tooltip = new Tooltip(action);
        Tooltip.install(button, tooltip);

        button.setStyle("-fx-background-radius: 32; " + SIDEBAR_BUTTON_STYLES);

        return button;
    }

    private Button createPrimaryAction(Ikon iconName, String action) {
        var button = new Button();
        var innerAvatar = new HBox();
        var avatar = new FontIcon(iconName);
        var name = new Label(action);
        innerAvatar.setSpacing(8);
        innerAvatar.setPadding(new Insets(4));
        innerAvatar.getChildren().addAll(avatar, name);
        innerAvatar.setAlignment(Pos.CENTER);

        button.setGraphic(innerAvatar);
        button.setStyle("-fx-background-radius: 16;");

        return button;
    }

    private HBox createChat(ReadOnlyDoubleProperty width, String initials, String message, boolean fromSelf) {
        var chat = new HBox();

        var avatarPlaceholder = this.createProfilePicture(initials);

        var chatContent = new Text(message);

        chatContent.wrappingWidthProperty().bind(width.subtract(48));

        if (fromSelf) {
            chat.getChildren().addAll(chatContent, avatarPlaceholder);
            chatContent.setTextAlignment(TextAlignment.RIGHT);
        } else {
            chat.getChildren().addAll(avatarPlaceholder, chatContent);
        }

        chat.setSpacing(8);
        chat.setAlignment(Pos.CENTER);

        return chat;
    }

    private Button createUserMenu(String initials, String fullName) {
        var avatarHeader = new Button();
        var innerAvatar = this.createUserPersona(initials, fullName);

        avatarHeader.setGraphic(innerAvatar);
        avatarHeader.setStyle("-fx-background-radius: 16; -fx-min-height: 64; -fx-max-height: 64");

        return avatarHeader;
    }

    private HBox createUserPersona(String initials, String fullName) {
        var innerAvatar = new HBox();
        var avatar = this.createProfilePicture(initials);

        var name = new Label(fullName);
        innerAvatar.setSpacing(8);
        innerAvatar.setPadding(new Insets(4));
        innerAvatar.getChildren().addAll(avatar, name);
        innerAvatar.setAlignment(Pos.CENTER_LEFT);

        return innerAvatar;
    }

    private Label createProfilePicture(String initials) {
        var avatar = new Label(initials);
        avatar.setAlignment(Pos.CENTER);
        avatar.setShape(new Circle(8));
        avatar.setStyle(
                "-fx-background-color: black; -fx-text-fill: white; -fx-min-width: 32; -fx-min-height: 32; -fx-max-width: 32; -fx-max-height: 32; -fx-font-size: 10; -fx-font-weight: bold;");
        avatar.setPadding(new Insets(8));

        return avatar;
    }

    private Label createHeader(String title) {
        var ownerHeader = new Label(title);

        ownerHeader.setStyle("-fx-font-weight: bold;");

        return ownerHeader;
    }

    private static String SIDEBAR_BUTTON_STYLES = "-fx-min-width: 64; -fx-min-height: 64; -fx-max-width: 64; -fx-max-height: 64; -fx-font-size: 16; -fx-font-weight: bold;";
}