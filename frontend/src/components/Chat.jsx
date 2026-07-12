import { useState, useEffect, useCallback } from 'react';
import { userApi, conversationApi, messageApi } from '../services/api';
import ConversationList from './ConversationList';
import MessageList from './MessageList';
import MessageInput from './MessageInput';

function Chat({ currentUser, onLogout }) {
  const [conversations, setConversations] = useState([]);
  const [activeConversation, setActiveConversation] = useState(null);
  const [messages, setMessages] = useState([]);
  const [allUsers, setAllUsers] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedUserIds, setSelectedUserIds] = useState([]);
  const [groupName, setGroupName] = useState('');

  const loadConversations = useCallback(async () => {
    try {
      const response = await conversationApi.getAll();
      if (response.data) {
        setConversations(response.data);
      }
    } catch (error) {
      console.error('Error loading conversations:', error);
    }
  }, []);

  const loadMessages = useCallback(async (conversationId) => {
    try {
      const response = await messageApi.getByConversationId(conversationId);
      if (response.data) {
        setMessages(response.data);
      }
    } catch (error) {
      console.error('Error loading messages:', error);
    }
  }, []);

  const loadUsers = useCallback(async () => {
    try {
      const response = await userApi.getAll();
      if (response.data) {
        setAllUsers(response.data);
      }
    } catch (error) {
      console.error('Error loading users:', error);
    }
  }, []);

  const createNewConversation = useCallback(async () => {
    if (selectedUserIds.length === 0) return;

    try {
      const isGroup = selectedUserIds.length >= 2;
      const userIds = [currentUser.id, ...selectedUserIds];
      
      const response = await conversationApi.create({
        userIds,
        isGroup,
        groupName: groupName && groupName.trim() !== '' ? groupName.trim() : undefined
      });
      
      if (response.data) {
        loadConversations();
        setIsModalOpen(false);
        setSelectedUserIds([]);
        setGroupName('');
        setActiveConversation(response.data);
        loadMessages(response.data.id);
      }
    } catch (error) {
      console.error('Error creating conversation:', error);
    }
  }, [selectedUserIds, currentUser.id, groupName, loadConversations, loadMessages]);

  const toggleUserSelection = (userId) => {
    setSelectedUserIds(prev => 
      prev.includes(userId) 
        ? prev.filter(id => id !== userId)
        : [...prev, userId]
    );
  };

  const handleSelectConversation = useCallback(async (conversation) => {
    setActiveConversation(conversation);
    loadMessages(conversation.id);
  }, [loadMessages]);

  const handleSendMessage = useCallback(async (content) => {
    if (!activeConversation) return;
    
    try {
      await messageApi.create({
        content,
        conversationId: activeConversation.id,
        userId: currentUser.id
      });
      loadMessages(activeConversation.id);
      loadConversations();
    } catch (error) {
      console.error('Error sending message:', error);
    }
  }, [activeConversation, currentUser.id, loadMessages, loadConversations]);

  const startChatWithUser = useCallback(async (user) => {
    let existingConversation = conversations.find(conv => {
      if (conv.isGroup) return false;
      const userIds = conv.users?.map(u => u.id) || [];
      return userIds.includes(currentUser.id) && userIds.includes(user.id);
    });

    if (!existingConversation) {
      existingConversation = await conversationApi.create({
        userIds: [currentUser.id, user.id],
        isGroup: false
      });
      if (existingConversation.data) {
        existingConversation = existingConversation.data;
        loadConversations();
      }
    }

    if (existingConversation) {
      handleSelectConversation(existingConversation);
    }
  }, [conversations, currentUser.id, handleSelectConversation, loadConversations]);

  useEffect(() => {
    loadConversations();
    loadUsers();
  }, [loadConversations, loadUsers]);

  useEffect(() => {
    const interval = setInterval(() => {
      if (activeConversation) {
        loadMessages(activeConversation.id);
      }
      loadConversations();
    }, 2000);

    return () => clearInterval(interval);
  }, [activeConversation, loadMessages, loadConversations]);

  const getConversationName = (conversation) => {
    if (!conversation) return '';
    if (conversation.isGroup && conversation.groupName) {
      return conversation.groupName;
    }
    const otherUsers = conversation.users?.filter(u => u.id !== currentUser.id);
    if (otherUsers && otherUsers.length > 0) {
      return otherUsers.map(u => u.username).join(', ');
    }
    return 'Cuộc trò chuyện';
  };

  const otherUsers = allUsers.filter(u => u.id !== currentUser.id);
  const isGroup = selectedUserIds.length >= 2;

  return (
    <div className="chat-container">
      <div className="sidebar">
        <div className="sidebar-header">
          <h3>Chat</h3>
          <div className="user-info">Xin chào, {currentUser.username}</div>
          <button className="btn-logout" onClick={onLogout}>Đăng xuất</button>
        </div>

        <div className="sidebar-actions">
          <button 
            className="btn-new-chat"
            onClick={() => setIsModalOpen(true)}
          >
            + Tạo cuộc trò chuyện mới
          </button>
        </div>
        
        <div style={{ padding: '0.5rem', borderBottom: '1px solid #ddd' }}>
          <div style={{ fontSize: '0.85rem', color: '#666', marginBottom: '0.5rem' }}>Người dùng khác:</div>
          {otherUsers.map(user => (
            <div
              key={user.id}
              className="conversation-item"
              onClick={() => startChatWithUser(user)}
            >
              <div className="conversation-name">{user.username}</div>
              <div className="conversation-preview">Nhấn để bắt đầu chat</div>
            </div>
          ))}
        </div>

        <div style={{ padding: '0.5rem', borderBottom: '1px solid #ddd', fontSize: '0.85rem', color: '#666' }}>
          Cuộc trò chuyện:
        </div>
        <ConversationList
          conversations={conversations}
          currentUser={currentUser}
          activeConversation={activeConversation}
          onSelectConversation={handleSelectConversation}
        />
      </div>

      <div className="main-chat">
        {activeConversation ? (
          <>
            <div className="chat-header">
              <h3>{getConversationName(activeConversation)}</h3>
            </div>
            <MessageList messages={messages} currentUser={currentUser} />
            <MessageInput onSendMessage={handleSendMessage} />
          </>
        ) : (
          <div className="no-conversation">
            Chọn người dùng để bắt đầu chat
          </div>
        )}
      </div>

      {isModalOpen && (
        <div className="modal-overlay" onClick={() => setIsModalOpen(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Tạo cuộc trò chuyện mới</h3>
              <button 
                className="btn-close"
                onClick={() => setIsModalOpen(false)}
              >
                ✕
              </button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label>Tên cuộc trò chuyện {isGroup && ' (nhóm)'}:</label>
                <input
                  type="text"
                  value={groupName}
                  onChange={(e) => setGroupName(e.target.value)}
                  placeholder={isGroup ? "Nhập tên nhóm..." : "Tên cuộc trò chuyện (tùy chọn)..."}
                />
                {selectedUserIds.length === 1 && (
                  <small style={{ color: '#666', marginTop: '0.25rem', display: 'block' }}>
                    Chỉ cần đặt tên khi tạo nhóm (≥2 người)
                  </small>
                )}
              </div>
              <div className="form-group">
                <label>Chọn người dùng:</label>
                <div className="user-list">
                  {otherUsers.map(user => (
                    <div 
                      key={user.id} 
                      className={`user-item ${selectedUserIds.includes(user.id) ? 'selected' : ''}`}
                      onClick={() => toggleUserSelection(user.id)}
                    >
                      <input
                        type="checkbox"
                        checked={selectedUserIds.includes(user.id)}
                        onChange={() => toggleUserSelection(user.id)}
                      />
                      <span>{user.username}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button 
                className="btn-cancel"
                onClick={() => setIsModalOpen(false)}
              >
                Hủy
              </button>
              <button 
                className="btn-create"
                onClick={createNewConversation}
                disabled={selectedUserIds.length === 0}
              >
                Tạo
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Chat;
