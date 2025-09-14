<script>
  import { onMount } from 'svelte';
  import CreatePoll from './CreatePoll.svelte';
  import VotePoll from './VotePoll.svelte';
  
  
  let users = [
    { id: 1, name: 'Alice' },
    { id: 2, name: 'Bob' },
    { id: 3, name: 'Charlie' },
  ];
  let selectedUserId = users[0].id; 
  let poll = null;
  let votes = [];
  let polls = [];
  let showCreateForm = false;
  let voteStatus = ''; 

  
  async function loadPolls() {
    try {
      const res = await fetch("http://localhost:8080/polls");
      if (res.ok) {
        polls = await res.json();
        
        if (polls.length > 0) {
          
          if (poll) {
            const currentPoll = polls.find(p => p.id === poll.id);
            if (currentPoll) {
              poll = currentPoll;
            } else {
              poll = polls[polls.length - 1];
            }
          } else {
            poll = polls[polls.length - 1];
          }
        }
      } else {
        polls = [];
      }
    } catch (error) {
      console.error("Error loading polls:", error);
      polls = [];
    }
  }

  async function handleCreate({ question, options }) {
    try {
      const res = await fetch('http://localhost:8080/polls', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ question }),
      });
      if (res.ok) {
        const pollData = await res.json();

        for (let i = 0; i < options.length; i++) {
          const caption = options[i].trim();
          if (!caption) continue;
          await fetch(`http://localhost:8080/polls/${pollData.id}/options`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
              caption: caption,
              presentationOrder: i + 1
            }),
          });
        }
        showCreateForm = false;
        await loadPolls();
      }
    } catch (error) {
      console.error('Error creating poll:', error);
    }
  }


  async function createInitialUsers() {
    if (!usersCreated) {
      try {
        
        for (const user of users) {
          const res = await fetch('http://localhost:8080/users', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              username: user.name,
              email: `${user.name.toLowerCase()}@example.com`
            })
          });
          if (res.ok) {
            console.log(`Created user: ${user.name}`);
          }
        }
        usersCreated = true;
      } catch (error) {
        console.error("Error creating users:", error);
      }
    }
  }

  
  let usersCreated = false;
  
  
  async function handleVote(optionId) {
    try {
      voteStatus = 'Submitting your vote...';
      
      
      if (!usersCreated) {
        await createInitialUsers();
      }
      
      
      const endpoint = `http://localhost:8080/votes?userId=${selectedUserId}&optionId=${optionId}`;
      console.log(`Submitting vote to: ${endpoint}`);
      
      const res = await fetch(endpoint, {
        method: 'POST',
      });
      
      if (res.ok) {
        
        const vote = await res.json();
        console.log("Vote created:", vote);
        
        voteStatus = 'Vote recorded successfully!';
        
        
        await loadPolls();
        
        
        try {
          const votesRes = await fetch('http://localhost:8080/votes');
          if (votesRes.ok) {
            const allVotes = await votesRes.json();
            console.log("Total votes in system:", allVotes.length);
          }
        } catch (e) {
          console.error("Error checking votes:", e);
        }
        
        setTimeout(() => voteStatus = '', 3000);
      } else {
        voteStatus = `Error: ${res.status} ${res.statusText}`;
        console.error("Vote failed:", res.status, res.statusText);
        setTimeout(() => voteStatus = '', 5000);
      }
    } catch (error) {
      voteStatus = 'Error submitting vote';
      console.error('Error voting:', error);
      setTimeout(() => voteStatus = '', 5000);
    }
  }

  async function resetPoll() {
    try {
      const res = await fetch('http://localhost:8080/polls', {
        method: 'DELETE',
      });
      if (res.ok) {
        poll = null;
        votes = [];
        await loadPolls();
      }
    } catch (error) {}
  }

  function selectPoll(pollItem) {
    poll = pollItem;
    voteStatus = '';
  }

  onMount(() => {
    loadPolls();
  });
</script>

<main>
  <h1>Polling Application</h1>
  
  <div class="polls-container">
    <div class="polls-list">
      <h2>Available Polls</h2>
      {#if polls.length > 0}
        <ul>
          {#each polls as pollItem}
            <li class={poll && poll.id === pollItem.id ? 'active' : ''}>
              <button on:click={() => selectPoll(pollItem)}>
                {pollItem.question}
              </button>
            </li>
          {/each}
        </ul>
      {:else}
        <p>No polls available.</p>
      {/if}
      
      <div class="action-buttons">
        <button class="create-btn" on:click={() => showCreateForm = true}>
          Create New Poll
        </button>
      </div>
    </div>
    
    <div class="poll-detail">
      {#if showCreateForm}
        <div class="create-poll-container">
          <h2>Create a New Poll</h2>
          <CreatePoll onCreate={handleCreate} />
          <button class="cancel-btn" on:click={() => showCreateForm = false}>
            Cancel
          </button>
        </div>
      {:else if poll}
        <div class="active-poll">
          <h2>{poll.question}</h2>
          
          <div class="user-selector">
            <label>
              Vote as:
              <select bind:value={selectedUserId}>
                {#each users as user}
                  <option value={user.id}>{user.name}</option>
                {/each}
              </select>
            </label>
          </div>
          
          {#if voteStatus}
            <div class="vote-status">
              {voteStatus}
            </div>
          {/if}
          
          <div class="options-list">
            {#if poll.options && poll.options.length > 0}
              {#each poll.options as option}
                <div class="option-item">
                  <div class="option-info">
                    <span class="option-text">{option.caption}</span>
                    <span class="vote-count">Votes: {option.votes ? option.votes.length : 0}</span>
                  </div>
                  <button class="vote-btn" on:click={() => handleVote(option.id)}>
                    Vote
                  </button>
                </div>
              {/each}
            {:else}
              <p>No options available for this poll.</p>
            {/if}
          </div>
          
          <button class="reset-btn" on:click={resetPoll}>
            Delete All Polls
          </button>
        </div>
      {:else}
        <div class="no-poll">
          <p>Select a poll or create a new one.</p>
          <button class="create-btn" on:click={() => showCreateForm = true}>
            Create New Poll
          </button>
        </div>
      {/if}
    </div>
  </div>
</main>

<style>
  main {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
    color: #fff; 
  }

  h1 {
    text-align: center;
    color: #ffffff; 
    margin-bottom: 30px;
    text-shadow: 0 0 10px rgba(255,255,255,0.3); 
  }

  h2 {
    color: #333; 
    margin-top: 0;
  }

  .polls-container {
    display: grid;
    grid-template-columns: 1fr 2fr;
    gap: 30px;
    background-color: #f9f9f9;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  }

  .polls-list {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    color: #333; 
  }

  .polls-list ul {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .polls-list li {
    margin-bottom: 10px;
  }

  .polls-list li button {
    width: 100%;
    text-align: left;
    padding: 12px;
    background: #f0f0f0;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s;
    color: #333; 
  }

  .polls-list li button:hover {
    background: #e0e0e0;
  }

  .polls-list li.active button {
    background: #007bff;
    color: white;
  }

  .poll-detail {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    color: #333; 
  }

  .active-poll h2 {
    border-bottom: 1px solid #eee;
    padding-bottom: 10px;
    margin-bottom: 20px;
    color: #333; 
  }

  .user-selector {
    margin-bottom: 20px;
    color: #333; 
  }

  .user-selector select {
    padding: 8px 12px;
    border-radius: 4px;
    border: 1px solid #ddd;
    background: #f5f5f5;
    color: #333; 
  }

  .options-list {
    margin: 20px 0;
  }

  .option-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #f9f9f9;
    padding: 12px 15px;
    border-radius: 4px;
    margin-bottom: 10px;
  }

  .option-info {
    display: flex;
    flex-direction: column;
  }

  .option-text {
    font-size: 16px;
    color: #333; 
    margin-bottom: 4px;
  }
  
  .vote-count {
    font-size: 14px;
    color: #777; 
  }

  .vote-btn {
    background: #28a745;
    color: white;
    border: none;
    border-radius: 4px;
    padding: 8px 16px;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .vote-btn:hover {
    background: #218838;
  }

  .create-btn {
    background: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    padding: 10px 16px;
    cursor: pointer;
    font-size: 16px;
    margin-top: 20px;
    transition: background-color 0.2s;
  }

  .create-btn:hover {
    background: #0069d9;
  }

  .cancel-btn {
    background: rgb(5, 131, 241);
    color: white;
    border: none;
    border-radius: 4px;
    padding: 10px 16px;
    cursor: pointer;
    margin-top: 10px;
  }

  .reset-btn {
    background: #dc3545;
    color: white;
    border: none;
    border-radius: 4px;
    padding: 10px 16px;
    cursor: pointer;
    margin-top: 20px;
  }

  .reset-btn:hover {
    background: #c82333;
  }

  .action-buttons {
    margin-top: 20px;
  }

  .no-poll {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 200px;
    color: #333; 
  }
</style>