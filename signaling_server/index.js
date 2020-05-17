var socketIO = require('socket.io'),
    http = require('http'),
    port = process.env.PORT || 8080,
    ip = process.env.IP || '0.0.0.0',
    server = http.createServer().listen(port, ip, function() {
        console.log("IP = ", ip);
        console.log("start socket successfully");
    });

io = socketIO.listen(server);
var currentConnections = {};
//io.set('match origin protocol', true);
io.set('origins', '*:*');

io.sockets.on('connection', function(socket) {
    socket.on("join-room", function(room) {
        console.log("joined");
        //Generate client ID
        //Generate id or query id here
        var id = "sample_id";

        socket.join(room);

        socket.on("answer", function(value) {
            console.log("sending answer ");
            socket.broadcast.to(room).emit('answer', value);
        });

        socket.on("offer", function(value) {
            console.log("sending offer ");
            socket.broadcast.to(room).emit('offer', value);
        });

        socket.on("established", function(value) {
            console.log("established ");
        });

        socket.on("ice-candidate", function(value) {
            socket.broadcast.to(room).emit('ice-candidate', value);
        });

        socket.on("leave-room", function(value) {
            console.log("leave room ");
            socket.broadcast.to(room).emit('leave-room', value);
        });
    });
});