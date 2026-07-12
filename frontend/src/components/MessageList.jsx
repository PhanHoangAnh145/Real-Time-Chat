import { useEffect, useRef } from 'react';

function MessageList({ messages, currentUser }) {
  const messagesEndRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  return (
    <div className="messages-container">
      {messages.map((message) => (
        <div
          key={message.id}
          className={`message ${message.user?.id === currentUser.id ? 'sent' : 'received'}`}
        >
          {message.user?.id !== currentUser.id && (
            <div className="message-author">{message.user?.username}</div>
          )}
          <div className="message-content">{message.content}</div>
        </div>
      ))}
      <div ref={messagesEndRef} />
    </div>
  );
}

export default MessageList;
