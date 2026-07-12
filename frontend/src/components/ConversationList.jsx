function ConversationList({ conversations, currentUser, activeConversation, onSelectConversation }) {
  const getConversationName = (conversation) => {
    if (conversation.isGroup && conversation.groupName) {
      return conversation.groupName;
    }
    const otherUsers = conversation.users?.filter(u => u.id !== currentUser.id);
    if (otherUsers && otherUsers.length > 0) {
      return otherUsers.map(u => u.username).join(', ');
    }
    return 'Cuộc trò chuyện';
  };

  return (
    <div className="conversation-list">
      {conversations.map((conversation) => (
        <div
          key={conversation.id}
          className={`conversation-item ${activeConversation?.id === conversation.id ? 'active' : ''}`}
          onClick={() => onSelectConversation(conversation)}
        >
          <div className="conversation-name">{getConversationName(conversation)}</div>
          <div className="conversation-preview">
            {conversation.messages?.length > 0 
              ? conversation.messages[conversation.messages.length - 1]?.content 
              : 'Chưa có tin nhắn'}
          </div>
        </div>
      ))}
    </div>
  );
}

export default ConversationList;
