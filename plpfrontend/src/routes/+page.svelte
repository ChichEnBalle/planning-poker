<script>
	let message = "Chargement...";
  
	async function fetchMessage() {
		const res = await fetch("http://backend:8080/api/hello");
		message = await res.text();
	}
  
	fetchMessage();
  </script>
  
  <h1>{message}</h1>
  